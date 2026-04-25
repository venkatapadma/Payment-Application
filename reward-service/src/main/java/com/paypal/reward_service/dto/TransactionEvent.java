package com.paypal.reward_service.dto;

import java.time.LocalDateTime;

public record TransactionEvent(Long id, Long senderId, Long recipientId, Double amount, LocalDateTime timestamp,
                               String status) {
}
