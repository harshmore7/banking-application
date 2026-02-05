package com.example.banking.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record OpenAccountRequest(
        @NotBlank String name,
        @NotBlank @Email String email,
        @NotBlank String accountType,
        @Min(0) double initialDeposit
) {
}
