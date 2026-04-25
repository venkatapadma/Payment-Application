package com.paypal.transaction_service.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public record DebitRequest(Long userId, String currency, BigDecimal amount) implements Serializable {
}
