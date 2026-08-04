package com.factoryops.analytics.controller;

import com.factoryops.analytics.dto.response.ApiResponse;
import com.factoryops.analytics.dto.response.DashboardResponse;
import com.factoryops.analytics.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboard() {

        DashboardResponse response = dashboardService.getDashboard();

        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Dashboard fetched successfully"
                )
        );
    }
}