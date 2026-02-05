package com.example.banking.controller;

import com.example.banking.dto.AccountOverviewResponse;
import com.example.banking.dto.AccountResponse;
import com.example.banking.dto.AmountRequest;
import com.example.banking.dto.OpenAccountRequest;
import com.example.banking.dto.TransactionResponse;
import com.example.banking.dto.TransferRequest;
import com.example.banking.dto.beneficiary.AddBeneficiaryRequest;
import com.example.banking.dto.beneficiary.BeneficiaryResponse;
import com.example.banking.model.Customer;
import com.example.banking.service.BankService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/accounts")
public class BankController {
    private final BankService bankService;

    public BankController(BankService bankService) {
        this.bankService = bankService;
    }

    @PostMapping
    public AccountResponse openAccount(Authentication authentication,
                                       @Valid @RequestBody OpenAccountRequest request) {
        Customer customer = (Customer) authentication.getPrincipal();
        return AccountResponse.from(bankService.openAccount(customer, request.accountType(), request.initialDeposit()));
    }

    @GetMapping("/me")
    public AccountOverviewResponse myOverview(Authentication authentication) {
        Customer customer = (Customer) authentication.getPrincipal();
        List<AccountResponse> accounts = bankService.listMyAccounts(customer.getCustomerId()).stream().map(AccountResponse::from).toList();
        return new AccountOverviewResponse(customer.getCustomerId(), customer.getFullName(), customer.getUsername(), customer.getEmail(), accounts);
    }

    @PostMapping("/{accountNumber}/deposit")
    public AccountResponse deposit(Authentication authentication,
                                   @PathVariable String accountNumber,
                                   @Valid @RequestBody AmountRequest request) {
        Customer customer = (Customer) authentication.getPrincipal();
        return AccountResponse.from(bankService.deposit(customer.getCustomerId(), accountNumber, request.amount()));
    }

    @PostMapping("/{accountNumber}/withdraw")
    public AccountResponse withdraw(Authentication authentication,
                                    @PathVariable String accountNumber,
                                    @Valid @RequestBody AmountRequest request) {
        Customer customer = (Customer) authentication.getPrincipal();
        return AccountResponse.from(bankService.withdraw(customer.getCustomerId(), accountNumber, request.amount()));
    }

    @PostMapping("/transfer")
    public Map<String, String> transfer(Authentication authentication,
                                        @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
                                        @Valid @RequestBody TransferRequest request) {
        Customer customer = (Customer) authentication.getPrincipal();
        String response = bankService.transfer(customer.getCustomerId(), request.fromAccount(), request.toAccount(), request.amount(), idempotencyKey);
        return Map.of("message", response);
    }

    @PostMapping("/{accountNumber}/beneficiaries")
    public BeneficiaryResponse addBeneficiary(Authentication authentication,
                                              @PathVariable String accountNumber,
                                              @Valid @RequestBody AddBeneficiaryRequest request) {
        Customer customer = (Customer) authentication.getPrincipal();
        return BeneficiaryResponse.from(bankService.addBeneficiary(customer.getCustomerId(), accountNumber, request.beneficiaryAccountNumber(), request.nickname()));
    }

    @GetMapping("/{accountNumber}/beneficiaries")
    public List<BeneficiaryResponse> listBeneficiaries(Authentication authentication,
                                                       @PathVariable String accountNumber) {
        Customer customer = (Customer) authentication.getPrincipal();
        return bankService.listBeneficiaries(customer.getCustomerId(), accountNumber).stream().map(BeneficiaryResponse::from).toList();
    }

    @PostMapping("/{accountNumber}/beneficiaries/{beneficiaryAccountNumber}/transfer")
    public Map<String, String> transferToBeneficiary(Authentication authentication,
                                                     @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
                                                     @PathVariable String accountNumber,
                                                     @PathVariable String beneficiaryAccountNumber,
                                                     @Valid @RequestBody AmountRequest request) {
        Customer customer = (Customer) authentication.getPrincipal();
        String response = bankService.transferToBeneficiary(customer.getCustomerId(), accountNumber, beneficiaryAccountNumber, request.amount(), idempotencyKey);
        return Map.of("message", response);
    }

    @GetMapping("/{accountNumber}/statement")
    public Page<TransactionResponse> statement(Authentication authentication,
                                               @PathVariable String accountNumber,
                                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
                                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
                                               @RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "20") int size) {
        Customer customer = (Customer) authentication.getPrincipal();
        return bankService.getStatement(customer.getCustomerId(), accountNumber, from, to, PageRequest.of(page, size))
                .map(TransactionResponse::from);
    }

    @PatchMapping("/{accountNumber}/status")
    public AccountResponse changeStatus(Authentication authentication,
                                        @PathVariable String accountNumber,
                                        @RequestParam String status) {
        Customer customer = (Customer) authentication.getPrincipal();
        return AccountResponse.from(bankService.changeAccountStatus(customer.getCustomerId(), accountNumber, status));
    }

    @GetMapping("/search")
    public List<AccountResponse> searchByCustomerName(@RequestParam String name) {
        return bankService.searchByCustomerName(name).stream().map(AccountResponse::from).toList();
    }
}
