package com.example.banking.dto;

import com.example.banking.model.Account;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AccountResponse(
        String accountNumber,
        String customerId,
        String customerName,
        String customerEmail,
        String accountType,
        String status,
        BigDecimal balance,
        LocalDateTime createdAt
) {
    public static AccountResponse from(Account account) {
        return new AccountResponse(
                account.getAccountNumber(),
                account.getCustomer().getCustomerId(),
                account.getCustomer().getFullName(),
                account.getCustomer().getEmail(),
                account.getAccountType(),
                account.getStatus().name(),
                account.getBalance(),
                account.getCreatedAt()
        );
    }
}
