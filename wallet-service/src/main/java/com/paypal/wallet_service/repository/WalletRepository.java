package com.paypal.wallet_service.repository;

import com.paypal.wallet_service.entity.Currency;
import com.paypal.wallet_service.entity.Wallet;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Wallet> findByUserIdAndCurrency(Long userId, Currency currency);

    Optional<Wallet> findByUserId(Long userId);

    //Optional<Wallet> findforUpdate(Long userId, Currency currency);
}
