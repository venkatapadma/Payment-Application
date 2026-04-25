package com.paypal.user_service.service;

import com.paypal.user_service.dto.JWTTokenResponse;
import com.paypal.user_service.dto.LoginRequest;
import com.paypal.user_service.dto.SignUpRequest;

public interface AuthService {

    JWTTokenResponse authenticate(LoginRequest loginRequest);

    Long createUser(SignUpRequest signUpRequest);
}
