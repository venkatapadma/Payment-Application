package com.paypal.notification_service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paypal.notification_service.dto.TransactionEvent;
import com.paypal.notification_service.entity.Notification;
import com.paypal.notification_service.repository.NotificationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class NotificationConsumer {

    private final NotificationRepository notificationRepository;

    private final ObjectMapper objectMapper;

    public NotificationConsumer(NotificationRepository notificationRepository, ObjectMapper objectMapper) {
        this.notificationRepository = notificationRepository;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "txn-initiated", groupId = "notification-group")
    public void consume(TransactionEvent transaction) {
        log.info("Transaction received: {}", transaction);

        Notification notification = new Notification();
        Long senderUserId = transaction.senderId();
        String notify = "$" + transaction.amount() + " received from " + senderUserId;
        notification.setUserId(senderUserId);
        notification.setMessage(notify);
        notification.setSentAt(LocalDateTime.now());
        log.info("Saving notification: {}", notification);

        notificationRepository.save(notification);
    }
}
