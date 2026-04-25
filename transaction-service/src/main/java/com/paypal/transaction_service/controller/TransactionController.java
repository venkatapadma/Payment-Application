package com.paypal.transaction_service.controller;

import com.paypal.transaction_service.dto.TransactionResponse;
import com.paypal.transaction_service.dto.TransferRequest;
import com.paypal.transaction_service.service.TransactionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SecurityRequirement(name = "BearerAuth")
@RestController
@RequestMapping("/api/v1/transactions/")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/create")
    public ResponseEntity<TransactionResponse> createTransaction(@Valid @RequestBody TransferRequest transferRequest) {
        TransactionResponse created = transactionService.createTransaction(transferRequest);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByUserId(@PathVariable Long userId) {
        List<TransactionResponse> transactionResponseList = transactionService.getTransactionsByUserId(userId);
        return ResponseEntity.ok(transactionResponseList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransactionById(@PathVariable Long id) {
        TransactionResponse transactionResponse = transactionService.getTransactionById(id);
        return ResponseEntity.ok(transactionResponse);
    }
}
