package com.paypal.wallet_service.service;

import com.paypal.wallet_service.dto.*;
import com.paypal.wallet_service.entity.*;
import com.paypal.wallet_service.exception.InsufficientFundsException;
import com.paypal.wallet_service.exception.NotFoundException;
import com.paypal.wallet_service.repository.TransactionRepository;
import com.paypal.wallet_service.repository.WalletHoldRepository;
import com.paypal.wallet_service.repository.WalletRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;

    private final WalletHoldRepository walletHoldRepository;

    private final TransactionRepository transactionRepository;


    public WalletServiceImpl(WalletRepository walletRepository, WalletHoldRepository walletHoldRepository, TransactionRepository transactionRepository) {
        this.walletRepository = walletRepository;
        this.walletHoldRepository = walletHoldRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    @Override
    public WalletResponse createWallet(CreateWalletRequest walletRequest) {
        Long userId = walletRequest.userId();
        Currency currency = walletRequest.currency();
        walletRepository.findByUserIdAndCurrency(userId, currency).ifPresent(w -> {
            throw new IllegalStateException("Wallet already exists for user" + userId);
        });

        Wallet wallet = new Wallet();
        wallet.setUserId(userId);
        wallet.setCurrency(currency);

        Wallet saved = walletRepository.save(wallet);
        log.info("Wallet created for user: {}", userId);
        return WalletResponse.from(saved);
    }

    @Transactional
    @Override
    public WalletResponse creditWallet(CreditRequest creditRequest) {

        log.info("Credit Wallet Reques received: userId = {}, amount = {}, currency = {}",
                creditRequest.userId(), creditRequest.amount(), creditRequest.currency());
        Wallet wallet = walletRepository.findByUserIdAndCurrency(
                creditRequest.userId(), creditRequest.currency()).orElseThrow(() -> new NotFoundException("Wallet not found for userId: " + creditRequest.userId()));
        wallet.setBalance(wallet.getBalance().add(creditRequest.amount()));
        wallet.setAvailableBalance(wallet.getAvailableBalance().add(creditRequest.amount()));
        Wallet savedWallet = walletRepository.save(wallet);
        transactionRepository.save(new Transaction(wallet.getId(), WalletStatus.CREDIT,
                creditRequest.amount(), "SUCCESS"));

        log.info("DEBIT done: walletId={}, amount={}, availableBalance={}", wallet.getId(), wallet.getBalance(), wallet.getAvailableBalance());
        return new WalletResponse(savedWallet.getId(), savedWallet.getUserId(), savedWallet.getCurrency(),
                savedWallet.getBalance(), savedWallet.getAvailableBalance());
    }

    @Transactional
    @Override
    public WalletResponse debitWallet(DebitRequest debitRequest) {

        log.info("Credit Wallet Reques received: userId = {}, amount = {}, currency = {}",
                debitRequest.userId(), debitRequest.amount(), debitRequest.currency());
        Wallet wallet = walletRepository.findByUserIdAndCurrency(
                debitRequest.userId(), debitRequest.currency()).orElseThrow(() -> new NotFoundException("Wallet not found for userId: " + debitRequest.userId()));

        if (wallet.getBalance().compareTo(debitRequest.amount()) < 0) {
            throw new InsufficientFundsException("Insufficient Balance");
        }
        wallet.setBalance(wallet.getBalance().subtract(debitRequest.amount()));
        wallet.setAvailableBalance(wallet.getAvailableBalance().subtract(debitRequest.amount()));
        Wallet savedWallet = walletRepository.save(wallet);
        transactionRepository.save(new Transaction(wallet.getId(), WalletStatus.DEBIT,
                debitRequest.amount(), "SUCCESS"));

        log.info("DEBIT done: walletId={}, amount={}, availableBalance={}", wallet.getId(), wallet.getBalance(), wallet.getAvailableBalance());
        return new WalletResponse(savedWallet.getId(), savedWallet.getUserId(), savedWallet.getCurrency(),
                savedWallet.getBalance(), savedWallet.getAvailableBalance());
    }

    @Override
    public WalletResponse getWallet(Long userId) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Wallet not found for user: {}" + userId));
        return new WalletResponse(wallet.getId(), wallet.getUserId(), wallet.getCurrency(),
                wallet.getBalance(), wallet.getAvailableBalance());
    }

    @Transactional
    @Override
    public HoldResponse placeHold(HoldRequest holdRequest) {
        Optional<Wallet> walletOptional = walletRepository.findByUserIdAndCurrency(holdRequest.userId(), holdRequest.currency());

        if (walletOptional.isEmpty()) {
            log.error("Wallet not found for userId: {}, currency {}", holdRequest.userId(), holdRequest.currency());
        }

        Wallet wallet = walletOptional.orElseThrow(() -> new NotFoundException("Wallet not found for userId: " + holdRequest.userId()));

        if (wallet.getAvailableBalance().compareTo(holdRequest.amount()) < 0) {
            throw new InsufficientFundsException("Not Enough balance to hold");
        }

        wallet.setAvailableBalance(wallet.getAvailableBalance().subtract(holdRequest.amount()));
        walletRepository.save(wallet);

        WalletHold hold = new WalletHold();
        hold.setWallet(wallet);
        hold.setAmount(holdRequest.amount());
        hold.setHoldReference("HOLD-" + System.currentTimeMillis());
        hold.setStatus(WalletStatus.HOLD);
        walletHoldRepository.save(hold);


        return new HoldResponse(hold.getHoldReference(), hold.getAmount(), hold.getStatus());
    }

    @Transactional
    @Override
    public WalletResponse captureHold(CaptureRequest request) {
        WalletHold hold = walletHoldRepository.findByHoldReference(request.holdReference()).orElseThrow(
                () -> new NotFoundException("Hold not found for referenceId: " + request.holdReference()));

        if (!WalletStatus.HOLD.equals(hold.getStatus())) {
            throw new IllegalStateException("Hold Status not active");
        }

        Wallet wallet = hold.getWallet();
        wallet.setBalance(wallet.getBalance().subtract(hold.getAmount()));

        hold.setStatus(WalletStatus.CAPTURE);
        walletRepository.save(wallet);
        walletHoldRepository.save(hold);

        return new WalletResponse(wallet.getId(), wallet.getUserId(),
                wallet.getCurrency(), wallet.getBalance(), wallet.getAvailableBalance());
    }

    @Transactional
    @Override
    public HoldResponse releaseHold(String holdReference) {
        WalletHold hold = walletHoldRepository.findByHoldReference(holdReference).orElseThrow(
                () -> new NotFoundException("Hold not found for referenceId: " + holdReference));

        if (!WalletStatus.HOLD.equals(hold.getStatus())) {
            throw new IllegalStateException("Hold Status not active");
        }

        Wallet wallet = hold.getWallet();
        wallet.setAvailableBalance(wallet.getAvailableBalance().add(hold.getAmount()));

        hold.setStatus(WalletStatus.RELEASE);
        walletRepository.save(wallet);
        walletHoldRepository.save(hold);

        return new HoldResponse(hold.getHoldReference(), hold.getAmount(), hold.getStatus());
    }
}
