package com.factoryops.auth.service.impl;

import com.factoryops.auth.dto.auth.LoginRequest;
import com.factoryops.auth.dto.auth.LoginResponse;
import com.factoryops.auth.entity.User;
import com.factoryops.auth.repository.UserRepository;
import com.factoryops.auth.security.CustomUserDetails;
import com.factoryops.auth.security.JwtService;
import com.factoryops.auth.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    private final JwtService jwtService;

    @Override
    public LoginResponse login(LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(

                new UsernamePasswordAuthenticationToken(

                        request.getUsername(),
                        request.getPassword()

                )
        );

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow();

        String token = jwtService.generateToken(userDetails);

        return LoginResponse.builder()

                .token(token)
                .tokenType("Bearer")
                .userId(user.getUserId())
                .username(user.getUsername())
                .role(user.getRole().getName().name())

                .build();
    }
}