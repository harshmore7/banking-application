package com.example.banking.repository;

import com.example.banking.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
    Page<Transaction> findByAccountAccountNumberOrderByTimestampDesc(String accountNumber, Pageable pageable);

    Page<Transaction> findByAccountAccountNumberAndTimestampBetweenOrderByTimestampDesc(
            String accountNumber,
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable
    );
}
