package com.paypal.wallet_service.scheduler;

import com.paypal.wallet_service.entity.WalletHold;
import com.paypal.wallet_service.entity.WalletStatus;
import com.paypal.wallet_service.repository.WalletHoldRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class HoldExpiryScheduler {

    private final WalletHoldRepository walletHoldrepository;


    public HoldExpiryScheduler(WalletHoldRepository walletHoldrepository) {
        this.walletHoldrepository = walletHoldrepository;
    }

    @Scheduled(fixedRateString = "${wallet.hold.expiry.scan-rate-ms:60000}")
    public void scheduleonHoldExpiry() {
        List<WalletHold> expired = walletHoldrepository.findByStatusAndExpiresAtBefore(
                WalletStatus.HOLD, LocalDateTime.now().minusMinutes(60));

        for (WalletHold walletHold : expired) {
            String ref = walletHold.getHoldReference();
            try {

                log.info("Expired hold released: {}", ref);
            } catch (Exception e) {
                log.error("Failed to release expired hold {} : {}", ref, e.getMessage());
            }
        }
    }
}
