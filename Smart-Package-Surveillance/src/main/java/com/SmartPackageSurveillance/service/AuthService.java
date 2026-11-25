package com.SmartPackageSurveillance.service;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.SmartPackageSurveillance.dto.LoginRequest;
import com.SmartPackageSurveillance.dto.LoginResponse;
import com.SmartPackageSurveillance.dto.RegisterRequest;
import com.SmartPackageSurveillance.entity.User;
import com.SmartPackageSurveillance.enumaration.UserRole;
import com.SmartPackageSurveillance.exception.EmailAlreadyExistsException;
import com.SmartPackageSurveillance.exception.InvalidCredentialsException;
import com.SmartPackageSurveillance.repository.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public void register(RegisterRequest dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }
        User user = new User();
        user.setName(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(UserRole.CUSTOMER);
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    public LoginResponse login(LoginRequest dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }
        String token = jwtService.generateToken(user.getEmail());
        return new LoginResponse(user.getId(), token, user.getName(), user.getEmail(), user.getRole());
    }
}
