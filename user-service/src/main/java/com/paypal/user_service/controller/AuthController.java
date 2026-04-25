package com.paypal.user_service.controller;

import com.paypal.user_service.dto.LoginRequest;
import com.paypal.user_service.dto.SignUpRequest;
import com.paypal.user_service.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "user API", description = "Operation related to users")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignUpRequest signUpRequest) {
        Long id = authService.createUser(signUpRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully " + id);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {

        return ResponseEntity.ok(authService.authenticate(loginRequest));
    }
}
