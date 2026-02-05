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
import com.example.banking.service.AuthService;
import com.example.banking.service.BankService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
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
@RequestMapping("/api/accounts")
public class BankController {
    private final BankService bankService;
    private final AuthService authService;

    public BankController(BankService bankService, AuthService authService) {
        this.bankService = bankService;
        this.authService = authService;
    }

    @PostMapping
    public AccountResponse openAccount(@RequestHeader("Authorization") String authHeader,
                                       @Valid @RequestBody OpenAccountRequest request) {
        Customer customer = currentCustomer(authHeader);
        return AccountResponse.from(bankService.openAccount(customer, request.accountType(), request.initialDeposit()));
    }

    @GetMapping("/me")
    public AccountOverviewResponse myOverview(@RequestHeader("Authorization") String authHeader) {
        Customer customer = currentCustomer(authHeader);
        List<AccountResponse> accounts = bankService.listMyAccounts(customer.getCustomerId()).stream().map(AccountResponse::from).toList();
        return new AccountOverviewResponse(customer.getCustomerId(), customer.getFullName(), customer.getUsername(), customer.getEmail(), accounts);
    }

    @PostMapping("/{accountNumber}/deposit")
    public AccountResponse deposit(@RequestHeader("Authorization") String authHeader,
                                   @PathVariable String accountNumber,
                                   @Valid @RequestBody AmountRequest request) {
        Customer customer = currentCustomer(authHeader);
        return AccountResponse.from(bankService.deposit(customer.getCustomerId(), accountNumber, request.amount()));
    }

    @PostMapping("/{accountNumber}/withdraw")
    public AccountResponse withdraw(@RequestHeader("Authorization") String authHeader,
                                    @PathVariable String accountNumber,
                                    @Valid @RequestBody AmountRequest request) {
        Customer customer = currentCustomer(authHeader);
        return AccountResponse.from(bankService.withdraw(customer.getCustomerId(), accountNumber, request.amount()));
    }

    @PostMapping("/transfer")
    public Map<String, String> transfer(@RequestHeader("Authorization") String authHeader,
                                        @Valid @RequestBody TransferRequest request) {
        Customer customer = currentCustomer(authHeader);
        bankService.transfer(customer.getCustomerId(), request.fromAccount(), request.toAccount(), request.amount());
        return Map.of("message", "Transfer successful");
    }

    @PostMapping("/{accountNumber}/beneficiaries")
    public BeneficiaryResponse addBeneficiary(@RequestHeader("Authorization") String authHeader,
                                              @PathVariable String accountNumber,
                                              @Valid @RequestBody AddBeneficiaryRequest request) {
        Customer customer = currentCustomer(authHeader);
        return BeneficiaryResponse.from(bankService.addBeneficiary(customer.getCustomerId(), accountNumber, request.beneficiaryAccountNumber(), request.nickname()));
    }

    @GetMapping("/{accountNumber}/beneficiaries")
    public List<BeneficiaryResponse> listBeneficiaries(@RequestHeader("Authorization") String authHeader,
                                                       @PathVariable String accountNumber) {
        Customer customer = currentCustomer(authHeader);
        return bankService.listBeneficiaries(customer.getCustomerId(), accountNumber).stream().map(BeneficiaryResponse::from).toList();
    }

    @PostMapping("/{accountNumber}/beneficiaries/{beneficiaryAccountNumber}/transfer")
    public Map<String, String> transferToBeneficiary(@RequestHeader("Authorization") String authHeader,
                                                     @PathVariable String accountNumber,
                                                     @PathVariable String beneficiaryAccountNumber,
                                                     @Valid @RequestBody AmountRequest request) {
        Customer customer = currentCustomer(authHeader);
        bankService.transferToBeneficiary(customer.getCustomerId(), accountNumber, beneficiaryAccountNumber, request.amount());
        return Map.of("message", "Beneficiary transfer successful");
    }

    @GetMapping("/{accountNumber}/statement")
    public List<TransactionResponse> statement(@RequestHeader("Authorization") String authHeader,
                                               @PathVariable String accountNumber,
                                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
                                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        Customer customer = currentCustomer(authHeader);
        return bankService.getStatement(customer.getCustomerId(), accountNumber, from, to).stream().map(TransactionResponse::from).toList();
    }

    @PatchMapping("/{accountNumber}/status")
    public AccountResponse changeStatus(@RequestHeader("Authorization") String authHeader,
                                        @PathVariable String accountNumber,
                                        @RequestParam String status) {
        Customer customer = currentCustomer(authHeader);
        return AccountResponse.from(bankService.changeAccountStatus(customer.getCustomerId(), accountNumber, status));
    }

    @GetMapping("/search")
    public List<AccountResponse> searchByCustomerName(@RequestParam String name) {
        return bankService.searchByCustomerName(name).stream().map(AccountResponse::from).toList();
    }

    private Customer currentCustomer(String authHeader) {
        String token = authHeader.replace("Bearer ", "").trim();
        return authService.validateToken(token);
    }
}
