package com.example.banking.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record OpenAccountRequest(
        @NotBlank String name,
        @NotBlank @Email String email,
        @NotBlank String accountType,
        @NotNull @DecimalMin(value = "0.00", inclusive = true) BigDecimal initialDeposit
) {
}
