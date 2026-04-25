package com.paypal.transaction_service.dto;

import com.paypal.transaction_service.entity.TransactionStatus;
import com.paypal.transaction_service.entity.WalletStatus;

import java.math.BigDecimal;

public record HoldResponse(String holdReference, BigDecimal amount, WalletStatus status) {
}
