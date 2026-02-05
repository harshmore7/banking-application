package com.example.banking.service;

import com.example.banking.model.Account;
import com.example.banking.model.Transaction;

import java.math.BigDecimal;
import java.util.List;

public interface BankService {
    Account openAccount(String name, String email, String accountType, BigDecimal initialDeposit);

    Account deposit(String accountNumber, BigDecimal amount);

    Account withdraw(String accountNumber, BigDecimal amount);

    void transfer(String fromAccount, String toAccount, BigDecimal amount);

    List<Transaction> getStatement(String accountNumber);

    List<Account> listAccounts();

    List<Account> searchByCustomerName(String name);
}
