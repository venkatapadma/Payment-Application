package com.paypal.wallet_service.dto;

import com.paypal.wallet_service.entity.Currency;

import java.io.Serializable;
import java.math.BigDecimal;

public record CreditRequest(Long userId, Currency currency, BigDecimal amount) implements Serializable {
}
