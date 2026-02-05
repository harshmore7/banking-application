package com.example.banking.dto;

import java.util.List;

public record AccountOverviewResponse(
        String customerId,
        String fullName,
        String username,
        String email,
        List<AccountResponse> accounts
) {
}
