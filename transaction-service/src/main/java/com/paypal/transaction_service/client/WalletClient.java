package com.paypal.transaction_service.client;

import com.paypal.transaction_service.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "wallet-service",
        url = "${wallet.service.url}"
)
public interface WalletClient {

    @PostMapping("/api/v1/wallets/debit")
    WalletResponse debit(@RequestBody CreditRequest creditRequest);

    @PostMapping("/api/v1/wallets/credit")
    WalletResponse credit(@RequestBody CreditRequest creditRequest);

    @PostMapping("/api/v1/wallets/hold")
    HoldResponse placeHold(@RequestBody HoldRequest holdRequest);

    @PostMapping("/api/v1/wallets/capture")
    WalletResponse captureHold(@RequestBody CaptureRequest captureRequest);

    @PostMapping("/api/v1/wallets/release/{holdReference}")
    HoldResponse release(@PathVariable("holdReference") String holdReference);

    @GetMapping("/api/v1/wallets/{userId}")
    WalletResponse getWallet(@PathVariable("userId") Long userId);
}
