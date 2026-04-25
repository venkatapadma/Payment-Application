package com.paypal.transaction_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transaction")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(nullable = false)//name = "sender_id",
    private Long senderId;

    @Column(nullable = false)//name = "receiver_id",
    private Long recipientId;

    @Column(nullable = false)
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    @Column(nullable = false)
    private Currency currency = Currency.INR;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionStatus status = TransactionStatus.PENDING;
}
