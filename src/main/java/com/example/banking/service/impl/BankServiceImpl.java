package com.example.banking.service.impl;

import com.example.banking.model.Account;
import com.example.banking.model.AccountStatus;
import com.example.banking.model.Beneficiary;
import com.example.banking.model.Customer;
import com.example.banking.model.ProcessedRequest;
import com.example.banking.model.Transaction;
import com.example.banking.model.TransactionType;
import com.example.banking.repository.AccountRepository;
import com.example.banking.repository.BeneficiaryRepository;
import com.example.banking.repository.CustomerRepository;
import com.example.banking.repository.ProcessedRequestRepository;
import com.example.banking.repository.TransactionRepository;
import com.example.banking.service.BankService;
import com.example.banking.service.support.IdGeneratorService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final BeneficiaryRepository beneficiaryRepository;
    private final ProcessedRequestRepository processedRequestRepository;
    private final IdGeneratorService idGeneratorService;

    public BankServiceImpl(AccountRepository accountRepository,
                           CustomerRepository customerRepository,
                           TransactionRepository transactionRepository,
                           BeneficiaryRepository beneficiaryRepository,
                           ProcessedRequestRepository processedRequestRepository,
                           IdGeneratorService idGeneratorService) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
        this.transactionRepository = transactionRepository;
        this.beneficiaryRepository = beneficiaryRepository;
        this.processedRequestRepository = processedRequestRepository;
        this.idGeneratorService = idGeneratorService;
    }

    @Override
    public Account openAccount(Customer customer, String accountType, BigDecimal initialDeposit) {
        Customer existingCustomer = customerRepository.findById(customer.getCustomerId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));

        Account account = accountRepository.save(new Account(
                idGeneratorService.nextAccountNumber(),
                existingCustomer,
                accountType.toUpperCase(Locale.ROOT),
                BigDecimal.ZERO,
                AccountStatus.ACTIVE,
                LocalDateTime.now()
        ));

        if (initialDeposit != null && initialDeposit.compareTo(BigDecimal.ZERO) > 0) {
            deposit(customer.getCustomerId(), account.getAccountNumber(), initialDeposit);
        }

        return account;
    }

    @Override
    public Account deposit(String requesterCustomerId, String accountNumber, BigDecimal amount) {
        Account account = getOwnedActiveAccount(requesterCustomerId, accountNumber);
        account.deposit(amount);
        recordTransaction(TransactionType.DEPOSIT, account, amount, "Cash deposit", "SELF");
        return accountRepository.save(account);
    }

    @Override
    public Account withdraw(String requesterCustomerId, String accountNumber, BigDecimal amount) {
        Account account = getOwnedActiveAccount(requesterCustomerId, accountNumber);
        if (account.getBalance().compareTo(amount) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient balance");
        }
        account.withdraw(amount);
        recordTransaction(TransactionType.WITHDRAW, account, amount, "ATM/Cash withdrawal", "SELF");
        return accountRepository.save(account);
    }

    @Override
    public String transfer(String requesterCustomerId, String fromAccount, String toAccount, BigDecimal amount, String idempotencyKey) {
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            ProcessedRequest existing = processedRequestRepository
                    .findByIdempotencyKeyAndCustomerIdAndOperation(idempotencyKey, requesterCustomerId, "TRANSFER")
                    .orElse(null);
            if (existing != null) {
                return existing.getResponseMessage();
            }
        }

        if (fromAccount.equals(toAccount)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Source and destination accounts must differ");
        }

        Account source = getOwnedActiveAccount(requesterCustomerId, fromAccount);
        Account destination = getActiveAccount(toAccount);

        if (source.getBalance().compareTo(amount) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient balance");
        }

        source.withdraw(amount);
        destination.deposit(amount);
        accountRepository.save(source);
        accountRepository.save(destination);

        String reference = UUID.randomUUID().toString();
        recordTransaction(TransactionType.TRANSFER_OUT, source, amount, "Transfer to " + toAccount, reference);
        recordTransaction(TransactionType.TRANSFER_IN, destination, amount, "Transfer from " + fromAccount, reference);

        String response = "Transfer successful. Ref=" + reference;
        storeIdempotentResult(idempotencyKey, requesterCustomerId, "TRANSFER", response);
        return response;
    }

    @Override
    public String transferToBeneficiary(String requesterCustomerId, String fromAccount, String beneficiaryAccount, BigDecimal amount, String idempotencyKey) {
        beneficiaryRepository.findByOwnerAccountAccountNumberAndBeneficiaryAccountAccountNumber(fromAccount, beneficiaryAccount)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Beneficiary not linked to this account"));
        return transfer(requesterCustomerId, fromAccount, beneficiaryAccount, amount, idempotencyKey);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Transaction> getStatement(String requesterCustomerId, String accountNumber, LocalDateTime from, LocalDateTime to, Pageable pageable) {
        getOwnedActiveAccount(requesterCustomerId, accountNumber);
        if (from != null && to != null) {
            return transactionRepository.findByAccountAccountNumberAndTimestampBetweenOrderByTimestampDesc(accountNumber, from, to, pageable);
        }
        return transactionRepository.findByAccountAccountNumberOrderByTimestampDesc(accountNumber, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Account> listMyAccounts(String requesterCustomerId) {
        Customer customer = customerRepository.findById(requesterCustomerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
        return accountRepository.findByCustomerOrderByAccountNumber(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Account> searchByCustomerName(String name) {
        return accountRepository.findByCustomerFullNameContainingIgnoreCaseOrderByAccountNumber(name);
    }

    @Override
    public Beneficiary addBeneficiary(String requesterCustomerId, String ownerAccountNumber, String beneficiaryAccountNumber, String nickname) {
        Account owner = getOwnedActiveAccount(requesterCustomerId, ownerAccountNumber);
        Account beneficiary = getActiveAccount(beneficiaryAccountNumber);
        if (owner.getAccountNumber().equals(beneficiary.getAccountNumber())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot add your own account as beneficiary");
        }
        if (beneficiaryRepository.findByOwnerAccountAccountNumberAndBeneficiaryAccountAccountNumber(ownerAccountNumber, beneficiaryAccountNumber).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Beneficiary already exists");
        }
        return beneficiaryRepository.save(new Beneficiary(owner, beneficiary, nickname, LocalDateTime.now()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Beneficiary> listBeneficiaries(String requesterCustomerId, String ownerAccountNumber) {
        getOwnedActiveAccount(requesterCustomerId, ownerAccountNumber);
        return beneficiaryRepository.findByOwnerAccountAccountNumberOrderByCreatedAtDesc(ownerAccountNumber);
    }

    @Override
    public Account changeAccountStatus(String requesterCustomerId, String accountNumber, String status) {
        Account account = getOwnedActiveAccount(requesterCustomerId, accountNumber);
        AccountStatus targetStatus;
        try {
            targetStatus = AccountStatus.valueOf(status.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status. Use ACTIVE/FROZEN/CLOSED");
        }
        account.setStatus(targetStatus);
        return accountRepository.save(account);
    }

    private Account getOwnedActiveAccount(String requesterCustomerId, String accountNumber) {
        Account account = accountRepository.findById(accountNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found: " + accountNumber));
        if (!account.getCustomer().getCustomerId().equals(requesterCustomerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized for this account");
        }
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Account is not active");
        }
        return account;
    }

    private Account getActiveAccount(String accountNumber) {
        Account account = accountRepository.findById(accountNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found: " + accountNumber));
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Destination account is not active");
        }
        return account;
    }

    private void storeIdempotentResult(String idempotencyKey, String customerId, String operation, String responseMessage) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            return;
        }
        processedRequestRepository.save(new ProcessedRequest(
                idempotencyKey,
                customerId,
                operation,
                responseMessage,
                LocalDateTime.now()
        ));
    }

    private void recordTransaction(TransactionType type, Account account, BigDecimal amount, String note, String referenceId) {
        transactionRepository.save(new Transaction(
                UUID.randomUUID().toString(),
                type,
                account,
                amount,
                LocalDateTime.now(),
                note,
                referenceId
        ));
    }
}
