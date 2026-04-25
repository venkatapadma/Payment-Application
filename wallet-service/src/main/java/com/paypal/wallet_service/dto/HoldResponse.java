package com.paypal.wallet_service.dto;

import com.paypal.wallet_service.entity.WalletStatus;

import java.math.BigDecimal;

public record HoldResponse(String holdReference, BigDecimal amount, WalletStatus status) {
}
