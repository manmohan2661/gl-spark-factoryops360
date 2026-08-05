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
                        .path("/api/v1/suppliers/**",
                                "/api/v1/supplier-performances/**")
                        .uri("lb://SUPPLIER-SERVICE"))


                // Inventory Service
                .route("inventory-service", r -> r
                        .path("/api/v1/inventories/**",
                                "/api/v1/materials/**",
                                "/api/v1/warehouses/**",
                                "/api/v1/inventory-transactions/**")
                        .uri("lb://INVENTORY-SERVICE"))


                // Production Service
                .route("production-service", r -> r
                        .path("/api/v1/production-orders/**",
                                "/api/v1/production-batches/**",
                                "/api/v1/machines/**",
                                "/api/v1/shifts/**",
                                "/api/v1/machine-maintenances/**")
                        .uri("lb://PRODUCTION-SERVICE"))


                // Quality Service
                .route("quality-service", r -> r
                        .path("/api/v1/quality-inspections/**",
                                "/api/v1/defects/**")
                        .uri("lb://QUALITY-SERVICE"))


                // Analytics Service
                .route("analytics-service", r -> r
                        .path("/api/v1/dashboard/**",
                                "/api/v1/alerts/**",
                                "/api/v1/notifications/**",
                                "/api/v1/audit-logs/**")
                        .uri("lb://ANALYTICS-SERVICE"))


                .build();
    }
}