package com.paypal.user_service.service;

import com.paypal.user_service.client.WalletClient;
import com.paypal.user_service.dto.CreateWalletRequest;
import com.paypal.user_service.dto.WalletResponse;
import com.paypal.user_service.entity.User;
import com.paypal.user_service.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final WalletClient walletClient;

    public UserServiceImpl(UserRepository userRepository, WalletClient walletClient) {
        this.userRepository = userRepository;
        this.walletClient = walletClient;
    }

    @Override
    public User createUser(User user) {
        User savedUser = userRepository.save(user);
        try {
            CreateWalletRequest walletRequest = new CreateWalletRequest(
                    savedUser.getId(), "GBP");
            WalletResponse wallet = walletClient.createWallet(walletRequest);
            log.info("Successfully created wallet for user {} - {}", savedUser.getId(), wallet.id());
        } catch (Exception ex) {
            log.error("Wallet creation failed for user: " + savedUser.getId(), ex);
            userRepository.deleteById(savedUser.getId()); //rollback
            throw new RuntimeException("Wallet creation failed, user rolled back", ex);
        }
        return savedUser;
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUser(String email) {
        return userRepository.findByEmail(email);
    }
}
