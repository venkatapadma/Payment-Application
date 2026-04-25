package com.paypal.reward_service.kafka;

import com.paypal.reward_service.dto.TransactionEvent;
import com.paypal.reward_service.entity.Reward;
import com.paypal.reward_service.repository.RewardRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class RewardConsumer {

    private final RewardRepository rewardRepository;

    public RewardConsumer(RewardRepository rewardRepository) {
        this.rewardRepository = rewardRepository;
    }

    @KafkaListener(topics = "txn-initiated", groupId = "reward-group")
    public void consumeTransaction(TransactionEvent transaction) {
        try {
            if (rewardRepository.existsByTransactionId(transaction.id())) {
                log.info("Reward already exists for transaction: ", transaction.id());
                return;
            }
            Reward reward = new Reward();
            reward.setUserId(transaction.senderId());
            reward.setPoints(transaction.amount());
            reward.setSentAt(LocalDateTime.now());
            reward.setTransactionId(transaction.id());

            rewardRepository.save(reward);
            log.info("Reward saved: {}", reward);
        } catch (Exception e) {
            throw new RuntimeException("Error while saving reward: " + e.getMessage());
        }
    }
}
