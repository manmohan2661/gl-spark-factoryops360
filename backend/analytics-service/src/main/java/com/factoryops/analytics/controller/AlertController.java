package com.factoryops.analytics.controller;

import com.factoryops.analytics.dto.request.AlertRequest;
import com.factoryops.analytics.dto.response.ApiResponse;
import com.factoryops.analytics.dto.response.AlertResponse;
import com.factoryops.analytics.service.AlertService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;

    @PostMapping
    public ResponseEntity<ApiResponse<AlertResponse>> create(@Valid @RequestBody AlertRequest request) {
        AlertResponse response = alertService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Alert created successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AlertResponse>> getById(@PathVariable Long id) {
        AlertResponse response = alertService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Alert retrieved successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AlertResponse>>> getAll() {
        List<AlertResponse> response = alertService.getAll();
        return ResponseEntity.ok(ApiResponse.success(response, "Alert list retrieved successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AlertResponse>> update(@PathVariable Long id,
                                                             @Valid @RequestBody AlertRequest request) {
        AlertResponse response = alertService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Alert updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        alertService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Alert deleted successfully"));
    }
}
