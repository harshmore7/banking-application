package com.example.banking.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "processed_requests")
public class ProcessedRequest {

    @Id
    @Column(nullable = false, updatable = false)
    private String idempotencyKey;

    @Column(nullable = false)
    private String customerId;

    @Column(nullable = false)
    private String operation;

    @Column(nullable = false)
    private String responseMessage;

    @Column(nullable = false)
    private LocalDateTime processedAt;

    protected ProcessedRequest() {
    }

    public ProcessedRequest(String idempotencyKey, String customerId, String operation, String responseMessage, LocalDateTime processedAt) {
        this.idempotencyKey = idempotencyKey;
        this.customerId = customerId;
        this.operation = operation;
        this.responseMessage = responseMessage;
        this.processedAt = processedAt;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getOperation() {
        return operation;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }
}
