package com.paypal.transaction_service.client;

import com.paypal.transaction_service.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "wallet-service", url = "http://localhost:8085/api/v1/wallets")
public interface WalletClient {

    @PostMapping("/debit")
    WalletResponse debit(@RequestBody CreditRequest creditRequest);

    @PostMapping("/credit")
    WalletResponse credit(@RequestBody CreditRequest creditRequest);

    @PostMapping("/hold")
    HoldResponse placeHold(@RequestBody HoldRequest holdRequest);

    @PostMapping("/capture")
    WalletResponse captureHold(@RequestBody CaptureRequest captureRequest);

    @PostMapping("/release/{holdReference}")
    HoldResponse release(@PathVariable("holdReference") String holdReference);

    @GetMapping("/{userId}")
    WalletResponse getWallet(@PathVariable("userId") Long userId);
}
