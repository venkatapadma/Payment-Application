package com.paypal.wallet_service.dto;

import com.paypal.wallet_service.entity.Currency;

import java.math.BigDecimal;

public record HoldRequest(Long userId, Currency currency, BigDecimal amount) {
}
