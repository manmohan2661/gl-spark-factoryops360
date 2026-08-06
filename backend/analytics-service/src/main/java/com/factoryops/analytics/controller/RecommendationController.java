package com.factoryops.analytics.controller;

import com.factoryops.analytics.dto.response.ApiResponse;
import com.factoryops.analytics.dto.response.RecommendationResponse;
import com.factoryops.analytics.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<RecommendationResponse>>> getRecommendations() {
        List<RecommendationResponse> recommendations = recommendationService.getRecommendations();
        return ResponseEntity.ok(ApiResponse.success(recommendations, "Recommendations generated successfully"));
    }
}
