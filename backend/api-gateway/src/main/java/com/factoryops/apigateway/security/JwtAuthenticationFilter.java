package com.factoryops.apigateway.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtService jwtService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             GatewayFilterChain chain) {

        String path = exchange.getRequest()
                .getURI()
                .getPath();


        // ==============================
        // PUBLIC ENDPOINTS
        // ==============================
        if (path.startsWith("/api/v1/auth")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.equals("/actuator/health")
                || path.equals("/actuator/info")) {

            return chain.filter(exchange);
        }


        // ==============================
        // JWT VALIDATION
        // ==============================

        String authHeader =
                exchange.getRequest()
                        .getHeaders()
                        .getFirst(HttpHeaders.AUTHORIZATION);


        if (authHeader == null || !authHeader.startsWith("Bearer ")) {

            log.warn("Unauthorized request: Missing or invalid Authorization header.");

            exchange.getResponse()
                    .setStatusCode(HttpStatus.UNAUTHORIZED);

            return exchange.getResponse()
                    .setComplete();
        }


        String token = authHeader.substring(7);


        if (!jwtService.isTokenValid(token)) {

            log.warn("Unauthorized request: Invalid or expired JWT token.");

            exchange.getResponse()
                    .setStatusCode(HttpStatus.UNAUTHORIZED);

            return exchange.getResponse()
                    .setComplete();
        }


        log.debug("JWT validated successfully for request: {}", path);

        try {
            String username = jwtService.extractUsername(token);
            String role = jwtService.extractRole(token);

            ServerWebExchange modifiedExchange = exchange.mutate()
                    .request(exchange.getRequest().mutate()
                            .header("X-User-Name", username)
                            .header("X-User-Role", role)
                            .build())
                    .build();

            return chain.filter(modifiedExchange);
        } catch (Exception e) {
            log.error("Failed to extract claims from token", e);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }


    @Override
    public int getOrder() {

        return -2;
    }
}