package com.example.Auth_backend.service;

import com.example.Auth_backend.dto.AuthResponse;
import com.example.Auth_backend.dto.LoginRequest;
import com.example.Auth_backend.dto.RegisterRequest;


public interface AuthService {

    String register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

}
