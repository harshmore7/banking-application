package com.example.banking.dto.beneficiary;

import jakarta.validation.constraints.NotBlank;

public record AddBeneficiaryRequest(
        @NotBlank String beneficiaryAccountNumber,
        @NotBlank String nickname
) {
}
