package com.paypal.user_service.service;

import com.paypal.user_service.dto.JWTTokenResponse;
import com.paypal.user_service.dto.LoginRequest;
import com.paypal.user_service.dto.SignUpRequest;
import com.paypal.user_service.entity.User;
import com.paypal.user_service.exception.InvalidCredentialsException;
import com.paypal.user_service.exception.NotFoundException;
import com.paypal.user_service.exception.UserAlreadyExistsException;
import com.paypal.user_service.util.JWTUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;

    public AuthServiceImpl(UserService userService, PasswordEncoder passwordEncoder, JWTUtil jwtUtil) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public JWTTokenResponse authenticate(LoginRequest loginRequest) {
        Optional<User> userOpt = userService.getUser(loginRequest.email());

        if (userOpt.isEmpty()) {
            throw new NotFoundException("User not found with email: " + loginRequest.email());
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole());
        String token = jwtUtil.generateToken(claims, user.getEmail());
        return new JWTTokenResponse(token);
    }

    @Override
    public Long createUser(SignUpRequest signUpRequest) {
        Optional<User> existingUser = userService.getUser(signUpRequest.email());

        if (existingUser.isPresent()) {
            throw new UserAlreadyExistsException("User already exists with email: " + signUpRequest.email());
        }
        User user = new User();
        user.setName(signUpRequest.name());
        user.setEmail(signUpRequest.email());
        user.setRole("ROLE_USER");
        user.setPassword(passwordEncoder.encode(signUpRequest.password()));
        User savedUser = userService.createUser(user);
        return savedUser.getId();
    }
}
