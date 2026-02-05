package com.example.banking.service;

import com.example.banking.dto.auth.LoginRequest;
import com.example.banking.dto.auth.RegisterRequest;
import com.example.banking.model.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
class BankServiceImplTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private BankService bankService;

    @Test
    void registerLoginAndOpenSecondAccount() {
        var registerResponse = authService.register(new RegisterRequest(
                "Alice Doe",
                "alice@bank.com",
                "alice1",
                "Password@123",
                "+12345678901",
                "SAVINGS",
                new BigDecimal("1000.00")
        ));

        var login = authService.login(new LoginRequest("alice1", "Password@123"));
        Customer customer = authService.validateToken(login.accessToken());

        var secondAccount = bankService.openAccount(customer, "CURRENT", new BigDecimal("500.00"));

        assertEquals(registerResponse.customerId(), customer.getCustomerId());
        assertFalse(secondAccount.getAccountNumber().isBlank());
        assertEquals(2, bankService.listMyAccounts(customer.getCustomerId()).size());
    }

    @Test
    void transferShouldBeIdempotentWithKey() {
        var bob = authService.register(new RegisterRequest(
                "Bob Doe",
                "bob@bank.com",
                "bob1",
                "Password@123",
                "+12345678902",
                "SAVINGS",
                new BigDecimal("300.00")
        ));
        var carol = authService.register(new RegisterRequest(
                "Carol Doe",
                "carol@bank.com",
                "carol1",
                "Password@123",
                "+12345678903",
                "SAVINGS",
                new BigDecimal("100.00")
        ));

        String m1 = bankService.transfer(bob.customerId(), bob.primaryAccountNumber(), carol.primaryAccountNumber(), new BigDecimal("50.00"), "idem-123");
        String m2 = bankService.transfer(bob.customerId(), bob.primaryAccountNumber(), carol.primaryAccountNumber(), new BigDecimal("50.00"), "idem-123");

        assertEquals(m1, m2);
        var page = bankService.getStatement(bob.customerId(), bob.primaryAccountNumber(), null, null, PageRequest.of(0, 20));
        assertEquals(1, page.getTotalElements());
    }
}
