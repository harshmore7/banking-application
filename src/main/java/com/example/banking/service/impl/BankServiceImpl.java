package com.example.banking.service.impl;

import com.example.banking.model.Account;
import com.example.banking.model.Customer;
import com.example.banking.model.Transaction;
import com.example.banking.model.TransactionType;
import com.example.banking.repository.AccountRepository;
import com.example.banking.repository.CustomerRepository;
import com.example.banking.repository.TransactionRepository;
import com.example.banking.service.BankService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@Transactional
public class BankServiceImpl implements BankService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final TransactionRepository transactionRepository;

    public BankServiceImpl(AccountRepository accountRepository,
                           CustomerRepository customerRepository,
                           TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Account openAccount(String name, String email, String accountType, BigDecimal initialDeposit) {
        String customerId = UUID.randomUUID().toString();
        Customer customer = customerRepository.save(new Customer(customerId, name, email));

        String accountNumber = String.format("AC%06d", accountRepository.count() + 1);
        Account account = accountRepository.save(new Account(
                accountNumber,
                customer,
                accountType.toUpperCase(Locale.ROOT),
                BigDecimal.ZERO
        ));

        if (initialDeposit != null && initialDeposit.compareTo(BigDecimal.ZERO) > 0) {
            return deposit(accountNumber, initialDeposit);
        }

        return account;
    }

    @Override
    public Account deposit(String accountNumber, BigDecimal amount) {
        Account account = getExistingAccount(accountNumber);
        account.deposit(amount);
        recordTransaction(TransactionType.DEPOSIT, account, amount, "Deposit");
        return accountRepository.save(account);
    }

    @Override
    public Account withdraw(String accountNumber, BigDecimal amount) {
        Account account = getExistingAccount(accountNumber);
        if (account.getBalance().compareTo(amount) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient balance");
        }
        account.withdraw(amount);
        recordTransaction(TransactionType.WITHDRAW, account, amount, "Withdraw");
        return accountRepository.save(account);
    }

    @Override
    public void transfer(String fromAccount, String toAccount, BigDecimal amount) {
        if (fromAccount.equals(toAccount)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Source and destination accounts must differ");
        }

        Account source = getExistingAccount(fromAccount);
        Account destination = getExistingAccount(toAccount);

        if (source.getBalance().compareTo(amount) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient balance");
        }

        source.withdraw(amount);
        destination.deposit(amount);

        accountRepository.save(source);
        accountRepository.save(destination);

        recordTransaction(TransactionType.TRANSFER_OUT, source, amount, "Transfer to " + toAccount);
        recordTransaction(TransactionType.TRANSFER_IN, destination, amount, "Transfer from " + fromAccount);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getStatement(String accountNumber) {
        getExistingAccount(accountNumber);
        return transactionRepository.findByAccountAccountNumberOrderByTimestampDesc(accountNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Account> listAccounts() {
        return accountRepository.findAll().stream()
                .sorted((a, b) -> a.getAccountNumber().compareToIgnoreCase(b.getAccountNumber()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Account> searchByCustomerName(String name) {
        return accountRepository.findByCustomerNameContainingIgnoreCaseOrderByAccountNumber(name);
    }

    private Account getExistingAccount(String accountNumber) {
        return accountRepository.findById(accountNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found: " + accountNumber));
    }

    private void recordTransaction(TransactionType type, Account account, BigDecimal amount, String note) {
        Transaction transaction = new Transaction(
                UUID.randomUUID().toString(),
                type,
                account,
                amount,
                LocalDateTime.now(),
                note
        );
        transactionRepository.save(transaction);
    }
}
