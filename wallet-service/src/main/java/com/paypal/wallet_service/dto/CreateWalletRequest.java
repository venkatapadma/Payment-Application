package com.paypal.wallet_service.dto;

import com.paypal.wallet_service.entity.Currency;

public record CreateWalletRequest(
        Long userId, Currency currency) {
}
