package com.paypal.api_gateway.filters;

import com.paypal.api_gateway.util.JWTUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Slf4j
public class JWTAuthFilter implements GlobalFilter, Ordered {

    private static final AntPathMatcher matcher = new AntPathMatcher();
    private static final List<String> PUBLIC_ENDPOINTS = List.of(
            "/user-service/api/v1/auth/**",
            "/swagger-ui/**",
            "/**/v3/api-docs",
            "/v3/api-docs"
    );
    private final JWTUtil jwtUtil;
    public JWTAuthFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
        log.info("JWTAuthFilter has been initialized");
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String normalizedPath = exchange.getRequest()
                .getPath()
                .value()
                .replaceAll("/+$", "");

        log.info("Incoming request path: {}", normalizedPath);

        //skip public endpoints
        if (PUBLIC_ENDPOINTS.stream().anyMatch(p -> matcher.match(p, normalizedPath))) {
            log.info("Public endpoint found, skipping auth");
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.info("Missing or invalid Authorization header");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        try {
            String token = authHeader.substring(7);
            Claims claims = jwtUtil.validateToken(token);

            ServerHttpRequest mutateRequest = exchange.getRequest().mutate()
                    .header("X-User-Email", claims.getSubject())
                    .header("X-User-Id", claims.get("userId", String.class))
                    .header("X-User-Role", claims.get("role", String.class))
                    .build();

            ServerWebExchange mutateExchange = exchange.mutate()
                    .request(mutateRequest)
                    .build();

            log.info("JWT validated successfully for {}", claims.getSubject());
            return chain.filter(mutateExchange);
        } catch (Exception e) {
            log.error("JWT validation failed", e);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
