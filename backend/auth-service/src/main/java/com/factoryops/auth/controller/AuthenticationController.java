package com.factoryops.auth.controller;

import com.factoryops.auth.dto.auth.LoginRequest;
import com.factoryops.auth.dto.auth.LoginResponse;
import com.factoryops.auth.dto.response.ApiResponse;
import com.factoryops.auth.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(

            @Valid
            @RequestBody LoginRequest request) {

        LoginResponse response =
                authenticationService.login(request);

        return ResponseEntity.ok(

                ApiResponse.success(
                        response,
                        "Login Successful"
                )
        );
    }
}