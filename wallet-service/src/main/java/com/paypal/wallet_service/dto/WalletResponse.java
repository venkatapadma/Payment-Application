package com.paypal.wallet_service.dto;

import com.paypal.wallet_service.entity.Currency;
import com.paypal.wallet_service.entity.Wallet;

import java.math.BigDecimal;

public record WalletResponse(
        Long id, Long userId, Currency currency,
        BigDecimal balance, BigDecimal availableBalance) {

    public static WalletResponse from(Wallet wallet) {
        return new WalletResponse(
                wallet.getId(),
                wallet.getUserId(),
                wallet.getCurrency(),
                wallet.getBalance(),
                wallet.getAvailableBalance());

    }
}
