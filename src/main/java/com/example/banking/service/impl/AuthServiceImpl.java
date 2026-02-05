package com.example.banking.service.impl;

import com.example.banking.dto.auth.AuthResponse;
import com.example.banking.dto.auth.LoginRequest;
import com.example.banking.dto.auth.RefreshTokenRequest;
import com.example.banking.dto.auth.RegisterRequest;
import com.example.banking.model.Account;
import com.example.banking.model.AccountStatus;
import com.example.banking.model.AuthToken;
import com.example.banking.model.Customer;
import com.example.banking.repository.AccountRepository;
import com.example.banking.repository.AuthTokenRepository;
import com.example.banking.repository.CustomerRepository;
import com.example.banking.security.JwtService;
import com.example.banking.service.AuthService;
import com.example.banking.service.support.IdGeneratorService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final AuthTokenRepository authTokenRepository;
    private final IdGeneratorService idGeneratorService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceImpl(CustomerRepository customerRepository,
                           AccountRepository accountRepository,
                           AuthTokenRepository authTokenRepository,
                           IdGeneratorService idGeneratorService,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService) {
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
        this.authTokenRepository = authTokenRepository;
        this.idGeneratorService = idGeneratorService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (customerRepository.existsByUsername(request.username())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username is already taken");
        }
        if (customerRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already registered");
        }
        if (customerRepository.existsByPhoneNumber(request.phoneNumber())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Phone number is already registered");
        }

        Customer customer = customerRepository.save(new Customer(
                idGeneratorService.nextCustomerId(),
                request.fullName(),
                request.email(),
                request.username(),
                passwordEncoder.encode(request.password()),
                request.phoneNumber(),
                LocalDateTime.now()
        ));

        Account account = accountRepository.save(new Account(
                idGeneratorService.nextAccountNumber(),
                customer,
                request.accountType().toUpperCase(Locale.ROOT),
                request.initialDeposit() == null ? BigDecimal.ZERO : request.initialDeposit(),
                AccountStatus.ACTIVE,
                LocalDateTime.now()
        ));

        return createSession(customer, account.getAccountNumber(), "Registration successful");
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        Customer customer = customerRepository.findByUsername(request.username())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), customer.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        String primaryAccountNumber = accountRepository.findByCustomerOrderByAccountNumber(customer).stream()
                .findFirst()
                .map(Account::getAccountNumber)
                .orElse("N/A");

        return createSession(customer, primaryAccountNumber, "Login successful");
    }

    @Override
    public AuthResponse refresh(RefreshTokenRequest request) {
        AuthToken refreshToken = authTokenRepository
                .findByTokenAndRevokedFalseAndExpiresAtAfter(request.refreshToken(), LocalDateTime.now())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token"));

        Customer customer = refreshToken.getCustomer();
        String primaryAccountNumber = accountRepository.findByCustomerOrderByAccountNumber(customer).stream()
                .findFirst().map(Account::getAccountNumber).orElse("N/A");

        return createSession(customer, primaryAccountNumber, "Token refreshed");
    }

    @Override
    @Transactional(readOnly = true)
    public Customer validateToken(String token) {
        String customerId;
        try {
            customerId = jwtService.customerId(token);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired access token");
        }
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token subject"));
    }

    private AuthResponse createSession(Customer customer, String primaryAccountNumber, String message) {
        authTokenRepository.deleteByCustomer(customer);
        String refreshToken = UUID.randomUUID().toString().replace("-", "") + UUID.randomUUID();
        authTokenRepository.save(new AuthToken(refreshToken, customer, LocalDateTime.now().plusDays(7), false));
        String accessToken = jwtService.generateAccessToken(customer.getCustomerId(), customer.getUsername());

        return new AuthResponse(
                accessToken,
                refreshToken,
                "Bearer",
                jwtService.accessTokenExpirationSeconds(),
                customer.getCustomerId(),
                customer.getUsername(),
                primaryAccountNumber,
                message
        );
    }
}
