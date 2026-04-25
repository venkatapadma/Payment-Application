package com.paypal.wallet_service.service;

import com.paypal.wallet_service.dto.*;
import jakarta.transaction.Transactional;

public interface WalletService {

    @Transactional
    WalletResponse createWallet(CreateWalletRequest walletRequest);

    @Transactional
    WalletResponse creditWallet(CreditRequest creditRequest);

    @Transactional
    WalletResponse debitWallet(DebitRequest debitRequest);

    WalletResponse getWallet(Long userId);

    @Transactional
    HoldResponse placeHold(HoldRequest holdRequest);

    @Transactional
    WalletResponse captureHold(CaptureRequest request);

    @Transactional
    HoldResponse releaseHold(String holdReference);
}
