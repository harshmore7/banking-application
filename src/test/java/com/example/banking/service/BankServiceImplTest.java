package com.example.banking.service;

import com.example.banking.model.Account;
import com.example.banking.repository.AccountRepository;
import com.example.banking.repository.TransactionRepository;
import com.example.banking.service.impl.BankServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BankServiceImplTest {

    private final BankService bankService = new BankServiceImpl(new AccountRepository(), new TransactionRepository());

    @Test
    void openAccountAndDepositAndWithdraw() {
        Account account = bankService.openAccount("Alice", "alice@example.com", "savings", 100);

        assertEquals("AC000001", account.getAccountNumber());
        assertEquals(100.0, account.getBalance());

        bankService.deposit(account.getAccountNumber(), 50.0);
        assertEquals(150.0, account.getBalance());

        bankService.withdraw(account.getAccountNumber(), 70.0);
        assertEquals(80.0, account.getBalance());
    }

    @Test
    void transferShouldMoveFundsBetweenAccounts() {
        Account from = bankService.openAccount("Bob", "bob@example.com", "current", 200);
        Account to = bankService.openAccount("Carol", "carol@example.com", "savings", 20);

        bankService.transfer(from.getAccountNumber(), to.getAccountNumber(), 40);

        assertEquals(160.0, from.getBalance());
        assertEquals(60.0, to.getBalance());
    }

    @Test
    void withdrawShouldFailWhenInsufficientBalance() {
        Account account = bankService.openAccount("Dave", "dave@example.com", "savings", 10);

        assertThrows(ResponseStatusException.class,
                () -> bankService.withdraw(account.getAccountNumber(), 50));
    }
}
