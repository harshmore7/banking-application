package com.example.banking.dto.beneficiary;

import com.example.banking.model.Beneficiary;

import java.time.LocalDateTime;

public record BeneficiaryResponse(
        String ownerAccountNumber,
        String beneficiaryAccountNumber,
        String beneficiaryName,
        String nickname,
        LocalDateTime createdAt
) {
    public static BeneficiaryResponse from(Beneficiary beneficiary) {
        return new BeneficiaryResponse(
                beneficiary.getOwnerAccount().getAccountNumber(),
                beneficiary.getBeneficiaryAccount().getAccountNumber(),
                beneficiary.getBeneficiaryAccount().getCustomer().getFullName(),
                beneficiary.getNickname(),
                beneficiary.getCreatedAt()
        );
    }
}
