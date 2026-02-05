package com.example.banking.model;

import java.time.LocalDateTime;

public class Transaction {
    private final String id;
    private final TransactionType type;
    private final String accountNumber;
    private final double amount;
    private final LocalDateTime timestamp;
    private final String note;

    public Transaction(String id, TransactionType type, String accountNumber, double amount, LocalDateTime timestamp, String note) {
        this.id = id;
        this.type = type;
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.timestamp = timestamp;
        this.note = note;
    }

    public String getId() {
        return id;
    }

    public TransactionType getType() {
        return type;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getNote() {
        return note;
    }
}
