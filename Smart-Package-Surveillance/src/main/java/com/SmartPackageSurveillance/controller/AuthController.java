package com.SmartPackageSurveillance.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.SmartPackageSurveillance.dto.LoginRequest;
import com.SmartPackageSurveillance.dto.LoginResponse;
import com.SmartPackageSurveillance.dto.RegisterRequest;
import com.SmartPackageSurveillance.dto.UserResponse;
import com.SmartPackageSurveillance.service.AuthService;
import com.SmartPackageSurveillance.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/auth/register")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody RegisterRequest dto) {
        authService.register(dto);
        return ResponseEntity.status(201).body(Map.of("message", "Account created"));
    }

    @PostMapping("/auth/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest dto) {
        return authService.login(dto);
    }

    @GetMapping("/users/me")
    public UserResponse me() {
        return userService.getCurrentUserProfile();
    }
}
