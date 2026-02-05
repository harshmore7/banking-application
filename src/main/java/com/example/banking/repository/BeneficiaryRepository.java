package com.example.banking.repository;

import com.example.banking.model.Beneficiary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BeneficiaryRepository extends JpaRepository<Beneficiary, Long> {
    List<Beneficiary> findByOwnerAccountAccountNumberOrderByCreatedAtDesc(String ownerAccountNumber);

    Optional<Beneficiary> findByOwnerAccountAccountNumberAndBeneficiaryAccountAccountNumber(String owner, String beneficiary);
}
