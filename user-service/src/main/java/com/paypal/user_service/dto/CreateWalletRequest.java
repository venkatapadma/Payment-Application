package com.paypal.user_service.dto;

public record CreateWalletRequest(Long userId, String currency) {
}
