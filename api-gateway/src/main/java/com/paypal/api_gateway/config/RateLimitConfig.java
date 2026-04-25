package com.paypal.api_gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;
import java.util.Optional;

@Configuration
public class RateLimitConfig {

    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> {
            String userId = exchange.getRequest().getHeaders().getFirst("X-User-Id");
            if (userId != null) {
                return Mono.just(userId);
            }

            //fallback via ip address
            Optional<InetSocketAddress> remoteAddress = Optional.ofNullable(exchange.getRequest().getRemoteAddress());
            return Mono.justOrEmpty(remoteAddress.map(address -> address.getAddress().getHostAddress()
            ));
        };
    }
}
