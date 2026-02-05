package com.example.banking.dto.auth;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record RegisterRequest(
        @NotBlank String fullName,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 4, max = 20) String username,
        @NotBlank @Size(min = 8, max = 100) String password,
        @NotBlank @Pattern(regexp = "^\\+?[0-9]{10,15}$") String phoneNumber,
        @NotBlank String accountType,
        @DecimalMin(value = "0.00", inclusive = true) BigDecimal initialDeposit
) {
}
