package com.factoryops.analytics.client;

import com.factoryops.analytics.config.FeignConfig;
import com.factoryops.analytics.dto.response.ApiResponse;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "production-service", configuration = FeignConfig.class)
public interface ProductionClient {

    @GetMapping("/api/v1/machines")
    ApiResponse<List<JsonNode>> getAllMachines();

    @GetMapping("/api/v1/production-orders")
    ApiResponse<List<JsonNode>> getAllProductionOrders();
}
