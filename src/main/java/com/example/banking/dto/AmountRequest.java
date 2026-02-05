package com.example.banking.dto;

import jakarta.validation.constraints.DecimalMin;

public record AmountRequest(
        @DecimalMin(value = "0.01", inclusive = true) double amount
) {
}
