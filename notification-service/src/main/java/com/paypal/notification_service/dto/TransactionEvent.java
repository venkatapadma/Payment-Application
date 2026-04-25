package com.paypal.notification_service.dto;

import java.time.LocalDateTime;

public record TransactionEvent(Long id, Long senderId, Long recipientId, Double amount, LocalDateTime timestamp,
                               String status) {
}
