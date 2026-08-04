package com.factoryops.auth.service.impl;

import com.factoryops.auth.dto.auth.LoginRequest;
import com.factoryops.auth.dto.auth.LoginResponse;
import com.factoryops.auth.entity.User;
import com.factoryops.auth.exception.ResourceNotFoundException;
import com.factoryops.auth.repository.UserRepository;
import com.factoryops.auth.security.CustomUserDetails;
import com.factoryops.auth.security.JwtService;
import com.factoryops.auth.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Override
    public LoginResponse login(LoginRequest request) {

        log.info("Login request received for username: {}", request.getUsername());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with username: " + request.getUsername()
                        )
                );

        String token = jwtService.generateToken(userDetails);

        log.info("Login successful for username: {}", request.getUsername());

        return LoginResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getUserId())
                .username(user.getUsername())
                .role(user.getRole().getName().name())
                .build();
    }
}