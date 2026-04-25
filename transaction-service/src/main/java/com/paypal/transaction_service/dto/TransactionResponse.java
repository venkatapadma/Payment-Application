package com.paypal.transaction_service.dto;

import com.paypal.transaction_service.entity.Currency;
import com.paypal.transaction_service.entity.Transaction;
import com.paypal.transaction_service.entity.TransactionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(Long id, Long senderId, Long recipientId, BigDecimal amount, Currency currency,
                                  LocalDateTime timestamp, TransactionStatus status) {
    public static TransactionResponse from(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getSenderId(),
                transaction.getRecipientId(),
                transaction.getAmount(),
                transaction.getCurrency(),
                transaction.getTimestamp(),
                transaction.getStatus()
        );
    }
}
