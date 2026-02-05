package com.example.banking.dto.auth;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresInSeconds,
        String customerId,
        String username,
        String primaryAccountNumber,
        String message
) {
}
