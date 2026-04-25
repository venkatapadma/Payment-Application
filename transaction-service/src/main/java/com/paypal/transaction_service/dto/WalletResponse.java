package com.paypal.transaction_service.dto;

import java.math.BigDecimal;

public record WalletResponse(
        Long id, Long userId, String currency,
        BigDecimal balance, BigDecimal availableBalance) {
}
