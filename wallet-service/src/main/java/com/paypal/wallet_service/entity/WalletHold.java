package com.paypal.wallet_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "wallet_holds")
@Getter
@Setter
public class WalletHold {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;

    @Column(nullable = false)
    private String holdReference;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private WalletStatus status = WalletStatus.HOLD; //ACTIVE, CAPTURED, RELEASED

    private LocalDateTime createdAt;

    private LocalDateTime expiresAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
