package com.paypal.transaction_service.filters;

import com.paypal.transaction_service.util.JWTUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
public class JWTRequestFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    public JWTRequestFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader!=null && authorizationHeader.startsWith("Bearer ")) {
            log.info("validating token");
            String jwtToken = authorizationHeader.substring(7);
            if (!jwtToken.isBlank()) {
                try {
                    Claims claims = jwtUtil.validateToken(jwtToken);
                    String userName = claims.getSubject();
                    String role = claims.get("role", String.class);
                    if (userName!=null && !userName.isBlank()) {
                        UsernamePasswordAuthenticationToken authentication
                                = new UsernamePasswordAuthenticationToken(userName,
                                null,
                                List.of(new SimpleGrantedAuthority(role))
                        );
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        log.info("JWT validated successfully for user: {}", userName);
                    }
                } catch (Exception e) {
                    log.warn("Exception occurred: {}", e.getMessage());
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
                    return;
                }
            }
        }
        //Always continue the chain
        filterChain.doFilter(request, response);
    }
}
