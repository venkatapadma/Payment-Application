package com.paypal.user_service.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

@Component
@Slf4j
public class JWTUtil {

    private final SecretKey signingKey;

    public JWTUtil(@Value("${security.jwt.secret}") String secret) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalArgumentException("JWT secret is not configured");
        }
        this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    public Claims validateToken(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String generateToken(Map<String, Object> claims, String email) {
        return Jwts.builder()
                .claims(claims)
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 86400000)) //1 day
                .signWith(signingKey, Jwts.SIG.HS256)
                .compact();
    }
}
