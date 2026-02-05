package com.example.banking.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

public record TransferRequest(
        @NotBlank String fromAccount,
        @NotBlank String toAccount,
        @DecimalMin(value = "0.01", inclusive = true) double amount
) {
}
