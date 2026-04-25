package com.paypal.reward_service.service;

import com.paypal.reward_service.entity.Reward;
import com.paypal.reward_service.repository.RewardRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RewardServiceImpl implements RewardService {

    private final RewardRepository rewardRepository;

    public RewardServiceImpl(RewardRepository rewardRepository) {
        this.rewardRepository = rewardRepository;
    }

    @Override
    public Reward sendReward(Reward rewards) {
        rewards.setSentAt(LocalDateTime.now());
        return rewardRepository.save(rewards);
    }

    @Override
    public List<Reward> getRewardsByUserId(Long userId) {

        return rewardRepository.findByUserId(userId);
    }

    @Override
    public List<Reward> getAllRewards() {
        return rewardRepository.findAll();
    }
}
