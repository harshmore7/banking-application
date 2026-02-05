package com.example.banking.service;

import com.example.banking.dto.auth.AuthResponse;
import com.example.banking.dto.auth.LoginRequest;
import com.example.banking.dto.auth.RefreshTokenRequest;
import com.example.banking.dto.auth.RegisterRequest;
import com.example.banking.model.Customer;

public interface AuthService {
    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refresh(RefreshTokenRequest request);

    Customer validateToken(String token);
}
