package com.example.banking.dto.auth;

public record AuthResponse(
        String token,
        String customerId,
        String username,
        String primaryAccountNumber,
        String message
) {
}
