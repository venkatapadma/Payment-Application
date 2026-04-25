package com.paypal.wallet_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transaction")
@Getter
@Setter
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(nullable = false)
    private Long walletId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WalletStatus type;

    @Column(nullable = false)
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    public Transaction(Long walletId, WalletStatus walletStatus,
                       BigDecimal amount, String success) {
        this.walletId = walletId;
        this.type = walletStatus;
        this.amount = amount;
        this.status = success;
    }
}
