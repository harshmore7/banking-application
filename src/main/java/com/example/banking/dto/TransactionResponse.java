package com.example.banking.dto;

import com.example.banking.model.Transaction;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record TransactionResponse(
        String id,
        String type,
        String accountNumber,
        double amount,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime timestamp,
        String note
) {
    public static TransactionResponse from(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getType().name(),
                transaction.getAccountNumber(),
                transaction.getAmount(),
                transaction.getTimestamp(),
                transaction.getNote()
        );
    }
}
