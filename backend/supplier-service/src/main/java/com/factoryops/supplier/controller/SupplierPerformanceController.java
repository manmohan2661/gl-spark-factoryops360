package com.factoryops.supplier.controller;

import com.factoryops.supplier.dto.request.SupplierPerformanceRequest;
import com.factoryops.supplier.dto.response.ApiResponse;
import com.factoryops.supplier.dto.response.SupplierPerformanceResponse;
import com.factoryops.supplier.service.SupplierPerformanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/supplier-performances")
@RequiredArgsConstructor
public class SupplierPerformanceController {

    private final SupplierPerformanceService supplierPerformanceService;

    @PostMapping
    public ResponseEntity<ApiResponse<SupplierPerformanceResponse>> create(@Valid @RequestBody SupplierPerformanceRequest request) {
        SupplierPerformanceResponse response = supplierPerformanceService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "SupplierPerformance created successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SupplierPerformanceResponse>> getById(@PathVariable Long id) {
        SupplierPerformanceResponse response = supplierPerformanceService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "SupplierPerformance retrieved successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SupplierPerformanceResponse>>> getAll() {
        List<SupplierPerformanceResponse> response = supplierPerformanceService.getAll();
        return ResponseEntity.ok(ApiResponse.success(response, "SupplierPerformance list retrieved successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SupplierPerformanceResponse>> update(@PathVariable Long id,
                                                             @Valid @RequestBody SupplierPerformanceRequest request) {
        SupplierPerformanceResponse response = supplierPerformanceService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "SupplierPerformance updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        supplierPerformanceService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "SupplierPerformance deleted successfully"));
    }
}
