package com.example.banking.dto;

import com.example.banking.model.Account;

import java.math.BigDecimal;

public record AccountResponse(
        String accountNumber,
        String customerId,
        String customerName,
        String customerEmail,
        String accountType,
        BigDecimal balance
) {
    public static AccountResponse from(Account account) {
        return new AccountResponse(
                account.getAccountNumber(),
                account.getCustomer().getCustomerId(),
                account.getCustomer().getName(),
                account.getCustomer().getEmail(),
                account.getAccountType(),
                account.getBalance()
        );
    }
}
