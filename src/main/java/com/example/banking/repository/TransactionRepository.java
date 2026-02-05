package com.example.banking.repository;

import com.example.banking.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
    List<Transaction> findByAccountAccountNumberOrderByTimestampDesc(String accountNumber);
}
