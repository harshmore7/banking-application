package com.example.banking.dto;

import com.example.banking.model.Account;

public record AccountResponse(
        String accountNumber,
        String customerId,
        String customerName,
        String customerEmail,
        String accountType,
        double balance
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
