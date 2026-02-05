package com.example.banking.repository;

import com.example.banking.model.Account;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class AccountRepository {
    private final Map<String, Account> accountsByNumber = new ConcurrentHashMap<>();

    public void save(Account account) {
        accountsByNumber.put(account.getAccountNumber(), account);
    }

    public Optional<Account> findByAccountNumber(String accountNumber) {
        return Optional.ofNullable(accountsByNumber.get(accountNumber));
    }

    public List<Account> findAll() {
        return new ArrayList<>(accountsByNumber.values());
    }
}
