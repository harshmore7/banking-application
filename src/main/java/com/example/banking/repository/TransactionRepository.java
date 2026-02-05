package com.example.banking.repository;

import com.example.banking.model.Transaction;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class TransactionRepository {
    private final List<Transaction> transactions = new ArrayList<>();

    public synchronized void save(Transaction transaction) {
        transactions.add(transaction);
    }

    public synchronized List<Transaction> findByAccountNumber(String accountNumber) {
        return transactions.stream()
                .filter(transaction -> transaction.getAccountNumber().equals(accountNumber))
                .collect(Collectors.toList());
    }
}
