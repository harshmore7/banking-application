package com.example.banking.service;

import com.example.banking.model.Account;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class BankServiceImplTest {

    @Autowired
    private BankService bankService;

    @Test
    void openAccountAndTransfer() {
        Account from = bankService.openAccount("Bob", "bob@example.com", "current", new BigDecimal("200.00"));
        Account to = bankService.openAccount("Carol", "carol@example.com", "savings", new BigDecimal("20.00"));

        bankService.transfer(from.getAccountNumber(), to.getAccountNumber(), new BigDecimal("40.00"));

        assertEquals(new BigDecimal("160.00"), bankService.listAccounts().stream()
                .filter(a -> a.getAccountNumber().equals(from.getAccountNumber()))
                .findFirst()
                .orElseThrow()
                .getBalance());
        assertEquals(new BigDecimal("60.00"), bankService.listAccounts().stream()
                .filter(a -> a.getAccountNumber().equals(to.getAccountNumber()))
                .findFirst()
                .orElseThrow()
                .getBalance());
    }

    @Test
    void withdrawShouldFailWhenInsufficientBalance() {
        Account account = bankService.openAccount("Dave", "dave@example.com", "savings", new BigDecimal("10.00"));

        assertThrows(ResponseStatusException.class,
                () -> bankService.withdraw(account.getAccountNumber(), new BigDecimal("50.00")));
    }
}
