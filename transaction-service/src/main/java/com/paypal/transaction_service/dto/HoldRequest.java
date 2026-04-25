package com.paypal.transaction_service.dto;

import com.paypal.transaction_service.entity.Currency;

import java.math.BigDecimal;

public record HoldRequest(Long userId, Currency currency, BigDecimal amount) {
    public static HoldRequest from(TransferRequest transferRequest) {
        return new HoldRequest(
                transferRequest.senderId(),
                transferRequest.currency(),
                transferRequest.amount()
        );
    }
}
