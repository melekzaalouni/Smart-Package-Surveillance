package com.SmartPackageSurveillance.config;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.SmartPackageSurveillance.entity.User;
import com.SmartPackageSurveillance.enumaration.UserRole;
import com.SmartPackageSurveillance.repository.UserRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner createAdmin(UserRepository users, PasswordEncoder encoder,
                                  @Value("${app.admin.email}") String email,
                                  @Value("${app.admin.password}") String password,
                                  @Value("${app.admin.name}") String name) {
        return args -> {
            if (!users.existsByEmail(email)) {
                User admin = new User();
                admin.setName(name);
                admin.setEmail(email);
                admin.setPassword(encoder.encode(password));
                admin.setRole(UserRole.ADMIN);
                admin.setCreatedAt(LocalDateTime.now());
                users.save(admin);
            }
        };
    }
}
