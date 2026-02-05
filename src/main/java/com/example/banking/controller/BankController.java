package com.example.banking.controller;

import com.example.banking.dto.AccountResponse;
import com.example.banking.dto.AmountRequest;
import com.example.banking.dto.OpenAccountRequest;
import com.example.banking.dto.TransactionResponse;
import com.example.banking.dto.TransferRequest;
import com.example.banking.service.BankService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
public class BankController {
    private final BankService bankService;

    public BankController(BankService bankService) {
        this.bankService = bankService;
    }

    @PostMapping
    public AccountResponse openAccount(@Valid @RequestBody OpenAccountRequest request) {
        return AccountResponse.from(bankService.openAccount(
                request.name(),
                request.email(),
                request.accountType(),
                request.initialDeposit())
        );
    }

    @PostMapping("/{accountNumber}/deposit")
    public AccountResponse deposit(@PathVariable String accountNumber, @Valid @RequestBody AmountRequest request) {
        return AccountResponse.from(bankService.deposit(accountNumber, request.amount()));
    }

    @PostMapping("/{accountNumber}/withdraw")
    public AccountResponse withdraw(@PathVariable String accountNumber, @Valid @RequestBody AmountRequest request) {
        return AccountResponse.from(bankService.withdraw(accountNumber, request.amount()));
    }

    @PostMapping("/transfer")
    public Map<String, String> transfer(@Valid @RequestBody TransferRequest request) {
        bankService.transfer(request.fromAccount(), request.toAccount(), request.amount());
        return Map.of("message", "Transfer successful");
    }

    @GetMapping
    public List<AccountResponse> listAccounts() {
        return bankService.listAccounts().stream().map(AccountResponse::from).toList();
    }

    @GetMapping("/search")
    public List<AccountResponse> searchByCustomerName(@RequestParam String name) {
        return bankService.searchByCustomerName(name).stream().map(AccountResponse::from).toList();
    }

    @GetMapping("/{accountNumber}/statement")
    public List<TransactionResponse> statement(@PathVariable String accountNumber) {
        return bankService.getStatement(accountNumber).stream().map(TransactionResponse::from).toList();
    }
}
