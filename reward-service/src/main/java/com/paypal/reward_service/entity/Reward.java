package com.paypal.reward_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "reward")
@Getter
@Setter
public class Reward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rewardId;

    private Long userId;

    private Double points;

    private LocalDateTime sentAt;

    @Column(unique = true)
    private Long transactionId;
}
