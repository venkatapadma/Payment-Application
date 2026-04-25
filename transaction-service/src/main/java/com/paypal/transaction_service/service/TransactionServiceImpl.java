package com.paypal.transaction_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paypal.transaction_service.client.WalletClient;
import com.paypal.transaction_service.dto.*;
import com.paypal.transaction_service.entity.Transaction;
import com.paypal.transaction_service.entity.TransactionStatus;
import com.paypal.transaction_service.exception.NotFoundException;
import com.paypal.transaction_service.kafka.KafkaEventProducer;
import com.paypal.transaction_service.repository.TransactionRepository;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final KafkaEventProducer kafkaEventProducer;
    private final WalletClient walletClient;

    public TransactionServiceImpl(TransactionRepository transactionRepository,
                                  KafkaEventProducer kafkaEventProducer,
                                  WalletClient walletClient) {

        this.transactionRepository = transactionRepository;
        this.kafkaEventProducer = kafkaEventProducer;
        this.walletClient = walletClient;
    }

    @Override
    public TransactionResponse createTransaction(TransferRequest transferRequest) {
        log.info("Transfer request received: {}", transferRequest);

        //step:0 Mark transaction as PENDING
        Transaction transaction = requestToEntityMapper(transferRequest);
        log.info("Saving transaction as pending");
        Transaction savedTransaction = transactionRepository.save(transaction);
        log.info("Saved transaction as pending: {}", savedTransaction);

        TransactionResponse transactionResponse = null;
        String holdReference = null;
        boolean captured = false;

        HoldRequest holdRequest = HoldRequest.from(transferRequest);
        try {
            //step1: place hold on sender wallet
            log.info("Holding request received: {}", holdRequest);
            HoldResponse holdResponse = walletClient.placeHold(holdRequest);

            //extract hold reference from response safely
            holdReference = Optional.ofNullable(holdResponse.holdReference()).orElseThrow(() -> new NotFoundException("Hold reference not found: " + holdResponse));
            log.info("Hold placed: {}", holdReference);

            //NEW: check receiver wallet exists before capture
            try {
                log.info("Checking receiver wallet in DB: {}", holdRequest);
                walletClient.getWallet(transferRequest.recipientId());
            } catch (FeignException e) {
                //receiver not found or other 4XX - release hold and fail the transaction.
                log.error("Failed to release hold {}: {}", holdReference, e.getMessage());
                releaseHold(holdReference);
                log.error("receiver wallet missing: hold released: {}", holdReference);
                savedTransaction.setStatus(TransactionStatus.FAILED);
                transactionRepository.save(savedTransaction);
                log.error("Transaction failed - receiver wallet missing: {}", savedTransaction);
                transactionResponse = TransactionResponse.from(savedTransaction);
                return transactionResponse;
            }

            //step 2: Capture hold -> debit sender wallet
            CaptureRequest captureRequest = new CaptureRequest(holdReference);
            try {
                log.info("Capture request received: {}", captureRequest);
                walletClient.captureHold(captureRequest);
                captured = true;
                log.info("Hold captured: sender debited");
            } catch (FeignException e) {
                //If capture failed, release hold and fail the transaction.
                log.error("Capture failed: status = {}, body " + "= {}",
                        e.status(), e.getMessage());
                releaseHold(holdReference);
                savedTransaction.setStatus(TransactionStatus.FAILED);
                savedTransaction = transactionRepository.save(savedTransaction);
                log.error("Transaction failed - capture failed: {}", savedTransaction);
                transactionResponse = TransactionResponse.from(savedTransaction);
                return transactionResponse;
            }

            //step 3: Credit receiver wallet
            try {
                CreditRequest credit = new CreditRequest(transferRequest.recipientId(), transferRequest.currency(), transferRequest.amount());
                log.info("Credit request received: {}", credit);
                WalletResponse creditEntity = walletClient.credit(credit);
                log.info("Receiver credited successfully: {}", creditEntity);
                // step 4: Mark transaction as SUCCESS
                savedTransaction.setStatus(TransactionStatus.SUCCESS);
                savedTransaction = transactionRepository.save(savedTransaction);
                log.info("Transaction SUCCESS: {}", savedTransaction);
            } catch (FeignException e) {
                //Credit failed after capture - perform compensating refund to sender
                log.error("Failed to credit receiver: status= {} error: {}", e.status(), e.getMessage());
                //Attempt to refund sender
                try {
                    CreditRequest creditRequest = new CreditRequest(transferRequest.senderId(), transferRequest.currency(), transferRequest.amount());
                    log.info("Initiating refund to sender: {}", creditRequest);
                    walletClient.credit(creditRequest);
                    log.info("Compensating refund to  sender succeeded");
                } catch (FeignException ex) {
                    log.error("Compensating refund to sender failed status: {}, error: {}", ex.status(), ex.getMessage());
                }
                savedTransaction.setStatus(TransactionStatus.FAILED);
                savedTransaction = transactionRepository.save(savedTransaction);
                log.info("Transaction failed - credit failed and refunded sender: {}", savedTransaction);
                transactionResponse = TransactionResponse.from(savedTransaction);
                return transactionResponse;
            }
        } catch (FeignException ex) {
            log.error("Wallet service failed with status: {}, error:{}", ex.status(), ex.getLocalizedMessage());
            if (Objects.nonNull(holdReference) && !captured) {
                releaseHold(holdReference);
            }
            savedTransaction.setStatus(TransactionStatus.FAILED);
            savedTransaction = transactionRepository.save(savedTransaction);
            log.error("Saved transaction as failed: {}", savedTransaction);
            transactionResponse = TransactionResponse.from(savedTransaction);
            return  transactionResponse;
        } catch (Exception ex) {
            log.error("Wallet service failed with error:{}", ex.getMessage());
            if (Objects.nonNull(holdReference) && !captured) {
                releaseHold(holdReference);
            }
            savedTransaction.setStatus(TransactionStatus.FAILED);
            savedTransaction = transactionRepository.save(savedTransaction);
            log.error("Saved transaction as failed: {}", savedTransaction);
            transactionResponse = TransactionResponse.from(savedTransaction);
            return transactionResponse;
        }
        transactionResponse = TransactionResponse.from(savedTransaction);
        //send kafka Event
        log.info("Sending transaction event to kafka");
        kafkaEvent(transactionResponse);
        return transactionResponse;
    }

    private @NonNull Transaction requestToEntityMapper(TransferRequest transferRequest) {
        Transaction transaction = new Transaction();
        transaction.setSenderId(transferRequest.senderId());
        transaction.setRecipientId(transferRequest.recipientId());
        transaction.setAmount(transferRequest.amount());
        transaction.setCurrency(transferRequest.currency());
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setStatus(TransactionStatus.PENDING);
        return transaction;
    }

    private void kafkaEvent(TransactionResponse transactionResponse) {
        try {
            String key = String.valueOf(transactionResponse.id());
            kafkaEventProducer.sendTransactionEvent(key, transactionResponse);
            log.info("Transaction sent to Kafka");
        } catch (Exception e) {
            log.error("Error while sending transaction to Kafka: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    private void releaseHold(String holdReference) {
        try {
            log.info("Attempting hold release");
            HoldResponse releaseResp = walletClient.release(holdReference);
            log.info("Released hold amount status:{}", releaseResp.status());
        } catch (Exception e) {
            log.error("Failed to release hold {}: {}", holdReference, e.getMessage());
        }
    }

    @Override
    public List<TransactionResponse> getTransactionsByUserId(Long userId) {
        return transactionRepository.findBySenderIdOrRecipientId(userId, userId)
                .stream()
                .map(TransactionResponse::from)
                .toList();
    }

    @Override
    public TransactionResponse getTransactionById(Long id) {
        Transaction transaction = transactionRepository.findById(id).orElseThrow(() -> new NotFoundException("Transaction Not Found with id: " + id));
        return TransactionResponse.from(transaction);
    }

}
