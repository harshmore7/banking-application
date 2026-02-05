package com.example.banking.service;

import com.example.banking.model.Account;
import com.example.banking.model.Transaction;

import java.util.List;

public interface BankService {
    Account openAccount(String name, String email, String accountType, double initialDeposit);

    Account deposit(String accountNumber, double amount);

    Account withdraw(String accountNumber, double amount);

    void transfer(String fromAccount, String toAccount, double amount);

    List<Transaction> getStatement(String accountNumber);

    List<Account> listAccounts();

    List<Account> searchByCustomerName(String name);
}
