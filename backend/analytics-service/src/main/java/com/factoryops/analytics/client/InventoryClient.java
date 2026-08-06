package com.factoryops.analytics.client;

import com.factoryops.analytics.config.FeignConfig;
import com.factoryops.analytics.dto.response.ApiResponse;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "inventory-service", configuration = FeignConfig.class)
public interface InventoryClient {

    @GetMapping("/api/v1/inventories")
    ApiResponse<List<JsonNode>> getAllInventories();

    @GetMapping("/api/v1/materials")
    ApiResponse<List<JsonNode>> getAllMaterials();
}
