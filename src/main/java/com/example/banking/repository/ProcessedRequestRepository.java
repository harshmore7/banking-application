package com.example.banking.repository;

import com.example.banking.model.ProcessedRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProcessedRequestRepository extends JpaRepository<ProcessedRequest, String> {
    Optional<ProcessedRequest> findByIdempotencyKeyAndCustomerIdAndOperation(String key, String customerId, String operation);
}
