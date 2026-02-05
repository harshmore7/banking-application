package com.example.banking.service;

import com.example.banking.model.Account;
import com.example.banking.model.Beneficiary;
import com.example.banking.model.Customer;
import com.example.banking.model.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface BankService {
    Account openAccount(Customer customer, String accountType, BigDecimal initialDeposit);

    Account deposit(String requesterCustomerId, String accountNumber, BigDecimal amount);

    Account withdraw(String requesterCustomerId, String accountNumber, BigDecimal amount);

    void transfer(String requesterCustomerId, String fromAccount, String toAccount, BigDecimal amount);

    void transferToBeneficiary(String requesterCustomerId, String fromAccount, String beneficiaryAccount, BigDecimal amount);

    List<Transaction> getStatement(String requesterCustomerId, String accountNumber, LocalDateTime from, LocalDateTime to);

    List<Account> listMyAccounts(String requesterCustomerId);

    List<Account> searchByCustomerName(String name);

    Beneficiary addBeneficiary(String requesterCustomerId, String ownerAccountNumber, String beneficiaryAccountNumber, String nickname);

    List<Beneficiary> listBeneficiaries(String requesterCustomerId, String ownerAccountNumber);

    Account changeAccountStatus(String requesterCustomerId, String accountNumber, String status);
}
