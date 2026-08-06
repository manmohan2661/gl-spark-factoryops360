package com.factoryops.analytics.client;

import com.factoryops.analytics.config.FeignConfig;
import com.factoryops.analytics.dto.response.ApiResponse;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "quality-service", configuration = FeignConfig.class)
public interface QualityClient {

    @GetMapping("/api/v1/defects")
    ApiResponse<List<JsonNode>> getAllDefects();

    @GetMapping("/api/v1/quality-inspections")
    ApiResponse<List<JsonNode>> getAllInspections();
}
