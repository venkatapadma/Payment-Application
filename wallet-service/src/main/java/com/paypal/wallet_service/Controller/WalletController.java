package com.paypal.wallet_service.Controller;

import com.paypal.wallet_service.dto.*;
import com.paypal.wallet_service.service.WalletService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@SecurityRequirement(name = "BearerAuth")
@RestController
@RequestMapping("/api/v1/wallets")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping
    public ResponseEntity<WalletResponse> createWallet(@RequestBody CreateWalletRequest createWalletRequest) {
        return ResponseEntity.ok(walletService.createWallet(createWalletRequest));
    }

    @PostMapping("/credit")
    public ResponseEntity<WalletResponse> credit(@RequestBody CreditRequest creditRequest) {
        return ResponseEntity.ok(walletService.creditWallet(creditRequest));
    }

    @PostMapping("/debit")
    public ResponseEntity<WalletResponse> debit(@RequestBody DebitRequest debitRequest) {
        return ResponseEntity.ok(walletService.debitWallet(debitRequest));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<WalletResponse> getWallet(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(walletService.getWallet(userId));
    }

    @PostMapping("/hold")
    public ResponseEntity<HoldResponse> placeHold(@RequestBody HoldRequest holdRequest) {
        return ResponseEntity.ok(walletService.placeHold(holdRequest));
    }

    @PostMapping("/capture")
    public ResponseEntity<WalletResponse> capture(@RequestBody CaptureRequest captureRequest) {
        return ResponseEntity.ok(walletService.captureHold(captureRequest));
    }

    @PostMapping("/release/{holdReference}")
    public ResponseEntity<HoldResponse> release(@PathVariable("holdReference") String holdReference) {
        return ResponseEntity.ok(walletService.releaseHold(holdReference));
    }
}
