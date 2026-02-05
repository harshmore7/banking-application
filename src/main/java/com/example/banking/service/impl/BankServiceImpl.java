package com.example.banking.service.impl;

import com.example.banking.model.Account;
import com.example.banking.model.Customer;
import com.example.banking.model.Transaction;
import com.example.banking.model.TransactionType;
import com.example.banking.repository.AccountRepository;
import com.example.banking.repository.TransactionRepository;
import com.example.banking.service.BankService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class BankServiceImpl implements BankService {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final AtomicInteger accountCounter = new AtomicInteger(0);

    public BankServiceImpl(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public synchronized Account openAccount(String name, String email, String accountType, double initialDeposit) {
        String customerId = UUID.randomUUID().toString();
        Customer customer = new Customer(customerId, name, email);
        String accountNumber = String.format("AC%06d", accountCounter.incrementAndGet());

        Account account = new Account(accountNumber, customer, accountType.toUpperCase(Locale.ROOT), 0);
        accountRepository.save(account);

        if (initialDeposit > 0) {
            deposit(accountNumber, initialDeposit);
        }

        return account;
    }

    @Override
    public synchronized Account deposit(String accountNumber, double amount) {
        Account account = getExistingAccount(accountNumber);
        account.deposit(amount);
        transactionRepository.save(new Transaction(
                UUID.randomUUID().toString(),
                TransactionType.DEPOSIT,
                accountNumber,
                amount,
                LocalDateTime.now(),
                "Deposit"
        ));
        return account;
    }

    @Override
    public synchronized Account withdraw(String accountNumber, double amount) {
        Account account = getExistingAccount(accountNumber);
        if (account.getBalance() < amount) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient balance");
        }
        account.withdraw(amount);
        transactionRepository.save(new Transaction(
                UUID.randomUUID().toString(),
                TransactionType.WITHDRAW,
                accountNumber,
                amount,
                LocalDateTime.now(),
                "Withdraw"
        ));
        return account;
    }

    @Override
    public synchronized void transfer(String fromAccount, String toAccount, double amount) {
        if (fromAccount.equals(toAccount)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Source and destination accounts must differ");
        }

        Account source = getExistingAccount(fromAccount);
        Account destination = getExistingAccount(toAccount);

        if (source.getBalance() < amount) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient balance");
        }

        source.withdraw(amount);
        destination.deposit(amount);

        LocalDateTime now = LocalDateTime.now();
        transactionRepository.save(new Transaction(UUID.randomUUID().toString(), TransactionType.TRANSFER_OUT, fromAccount, amount, now,
                "Transfer to " + toAccount));
        transactionRepository.save(new Transaction(UUID.randomUUID().toString(), TransactionType.TRANSFER_IN, toAccount, amount, now,
                "Transfer from " + fromAccount));
    }

    @Override
    public List<Transaction> getStatement(String accountNumber) {
        getExistingAccount(accountNumber);
        return transactionRepository.findByAccountNumber(accountNumber).stream()
                .sorted(Comparator.comparing(Transaction::getTimestamp).reversed())
                .toList();
    }

    @Override
    public List<Account> listAccounts() {
        return accountRepository.findAll().stream()
                .sorted(Comparator.comparing(Account::getAccountNumber))
                .toList();
    }

    @Override
    public List<Account> searchByCustomerName(String name) {
        String normalizedQuery = name.toLowerCase(Locale.ROOT).trim();
        return accountRepository.findAll().stream()
                .filter(account -> account.getCustomer().getName().toLowerCase(Locale.ROOT).contains(normalizedQuery))
                .sorted(Comparator.comparing(Account::getAccountNumber))
                .toList();
    }

    private Account getExistingAccount(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found: " + accountNumber));
    }
}
