package com.example.banking.repository;

import com.example.banking.model.AuthToken;
import com.example.banking.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface AuthTokenRepository extends JpaRepository<AuthToken, String> {
    Optional<AuthToken> findByTokenAndRevokedFalseAndExpiresAtAfter(String token, LocalDateTime now);

    void deleteByCustomer(Customer customer);
}
