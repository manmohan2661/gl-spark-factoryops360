package com.factoryops.analytics.client;

import com.factoryops.analytics.config.FeignConfig;
import com.factoryops.analytics.dto.response.ApiResponse;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "supplier-service", configuration = FeignConfig.class)
public interface SupplierClient {

    @GetMapping("/api/v1/supplier-performances")
    ApiResponse<List<JsonNode>> getAllSupplierPerformances();

    @GetMapping("/api/v1/suppliers")
    ApiResponse<List<JsonNode>> getAllSuppliers();
}
