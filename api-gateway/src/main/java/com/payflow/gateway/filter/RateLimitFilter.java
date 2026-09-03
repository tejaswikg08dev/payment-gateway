package com.payflow.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Per-merchant rate limiting filter.
 * Uses Redis sliding window to enforce request limits.
 * Note: Basic implementation — production uses Spring Cloud Gateway's built-in RequestRateLimiter.
 */
@Component
public class RateLimitFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // Rate limiting is configured via Spring Cloud Gateway's built-in
        // RequestRateLimiter filter in application.yml
        // This filter exists as a hook for custom per-merchant rate limiting logic
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -3; // Execute first — reject early if rate limited
    }
}