package com.paypal.transaction_service.dto;

import com.paypal.transaction_service.entity.Currency;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransferRequest(@NotNull Long senderId, @NotNull Long recipientId, @NotNull BigDecimal amount,
                              @NotNull Currency currency) {
}
