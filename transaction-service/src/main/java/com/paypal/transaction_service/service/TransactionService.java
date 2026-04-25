package com.paypal.transaction_service.service;

import com.paypal.transaction_service.dto.TransactionResponse;
import com.paypal.transaction_service.dto.TransferRequest;

import java.util.List;

public interface TransactionService {

    TransactionResponse createTransaction(TransferRequest transferRequest);

    List<TransactionResponse> getTransactionsByUserId(Long userId);


    TransactionResponse getTransactionById(Long id);
}
