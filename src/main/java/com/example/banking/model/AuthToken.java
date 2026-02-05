package com.example.banking.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "auth_tokens")
public class AuthToken {

    @Id
    @Column(nullable = false, updatable = false)
    private String token;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private boolean revoked;

    protected AuthToken() {
    }

    public AuthToken(String token, Customer customer, LocalDateTime expiresAt, boolean revoked) {
        this.token = token;
        this.customer = customer;
        this.expiresAt = expiresAt;
        this.revoked = revoked;
    }

    public String getToken() {
        return token;
    }

    public Customer getCustomer() {
        return customer;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public void revoke() {
        this.revoked = true;
    }
}
