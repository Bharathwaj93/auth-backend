package com.example.Auth_backend.controller;

import com.example.Auth_backend.service.UserService;
import jakarta.validation.Valid;
import com.example.Auth_backend.dto.AuthResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.Auth_backend.dto.LoginRequest;
import com.example.Auth_backend.dto.RegisterRequest;
import com.example.Auth_backend.service.AuthService;
import com.example.Auth_backend.dto.ForgotPasswordRequest;
import com.example.Auth_backend.dto.VerifyOtpRequest;
import com.example.Auth_backend.dto.ResetPasswordRequest;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService,
                          UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/register")
    public String register(@Valid @RequestBody RegisterRequest request) {
        System.out.println("REGISTER API CALLED");
        return authService.register(request);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {

        System.out.println("LOGIN API CALLED");

        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(
            @RequestBody ForgotPasswordRequest request) {

        System.out.println("Forgot Password API Called");
        System.out.println("Email = " + request.getEmail());

        userService.forgotPassword(request.getEmail());

        return ResponseEntity.ok("OTP sent successfully");
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(
            @RequestBody VerifyOtpRequest request) {

        boolean verified = userService.verifyOtp(
                request.getEmail(),
                request.getOtp()
        );

        if (verified) {
            return ResponseEntity.ok("OTP verified successfully");
        }

        return ResponseEntity.badRequest().body("Invalid OTP");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestBody ResetPasswordRequest request) {

        userService.resetPassword(
                request.getEmail(),
                request.getNewPassword(),
                request.getConfirmPassword()
        );

        return ResponseEntity.ok("Password reset successfully");
    }
}
