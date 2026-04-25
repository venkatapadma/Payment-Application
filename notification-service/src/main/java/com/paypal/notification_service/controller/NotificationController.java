package com.paypal.notification_service.controller;

import com.paypal.notification_service.entity.Notification;
import com.paypal.notification_service.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    public ResponseEntity<Notification> save(@RequestBody Notification notification) {
        return ResponseEntity.ok(notificationService.sendNotification(notification));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Notification>> getNotificationsByUser(@PathVariable String userId) {
        return ResponseEntity.ok(notificationService.getNotificationsByUserId(userId));
    }
}
