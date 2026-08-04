package com.factoryops.auth.service;

import com.factoryops.auth.dto.auth.LoginRequest;
import com.factoryops.auth.dto.auth.LoginResponse;

public interface AuthenticationService {

    LoginResponse login(LoginRequest request);

}