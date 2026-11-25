package com.SmartPackageSurveillance.security;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.SmartPackageSurveillance.repository.UserRepository;
import com.SmartPackageSurveillance.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                String email = jwtService.extractEmail(header.substring(7));
                userRepository.findByEmail(email).ifPresent(user -> {
                    var auth = new UsernamePasswordAuthenticationToken(user.getEmail(), null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                });
            } catch (Exception e) {
                // invalid or expired token: request stays unauthenticated -> 401/403
            }
        }
        chain.doFilter(request, response);
    }
}
