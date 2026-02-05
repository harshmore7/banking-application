package com.example.banking.service;

import com.example.banking.model.Account;
import com.example.banking.model.Beneficiary;
import com.example.banking.model.Customer;
import com.example.banking.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface BankService {
    Account openAccount(Customer customer, String accountType, BigDecimal initialDeposit);

    Account deposit(String requesterCustomerId, String accountNumber, BigDecimal amount);

    Account withdraw(String requesterCustomerId, String accountNumber, BigDecimal amount);

    String transfer(String requesterCustomerId, String fromAccount, String toAccount, BigDecimal amount, String idempotencyKey);

    String transferToBeneficiary(String requesterCustomerId, String fromAccount, String beneficiaryAccount, BigDecimal amount, String idempotencyKey);

    Page<Transaction> getStatement(String requesterCustomerId, String accountNumber, LocalDateTime from, LocalDateTime to, Pageable pageable);

    List<Account> listMyAccounts(String requesterCustomerId);

    List<Account> searchByCustomerName(String name);

    Beneficiary addBeneficiary(String requesterCustomerId, String ownerAccountNumber, String beneficiaryAccountNumber, String nickname);

    List<Beneficiary> listBeneficiaries(String requesterCustomerId, String ownerAccountNumber);

    Account changeAccountStatus(String requesterCustomerId, String accountNumber, String status);
}
