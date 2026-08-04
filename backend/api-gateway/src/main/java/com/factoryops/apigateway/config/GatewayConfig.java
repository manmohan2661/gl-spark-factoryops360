package com.factoryops.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRoutes(RouteLocatorBuilder builder) {

        return builder.routes()

                // Auth Service
                .route("auth-service", r -> r
                        .path("/api/v1/auth/**")
                        .uri("lb://AUTH-SERVICE"))

                // Supplier Service
                .route("supplier-service", r -> r
                        .path("/api/v1/suppliers/**")
                        .uri("lb://SUPPLIER-SERVICE"))

                // Inventory Service
                .route("inventory-service", r -> r
                        .path("/api/v1/inventories/**")
                        .uri("lb://INVENTORY-SERVICE"))

                // Production Service
                .route("production-service", r -> r
                        .path("/api/v1/production-orders/**")
                        .uri("lb://PRODUCTION-SERVICE"))

                // Quality Service
                .route("quality-service", r -> r
                        .path("/api/v1/quality-inspections/**")
                        .uri("lb://QUALITY-SERVICE"))

                // Analytics Service
                .route("analytics-service", r -> r
                        .path("/api/v1/dashboard/**")
                        .uri("lb://ANALYTICS-SERVICE"))

                .build();
    }
}