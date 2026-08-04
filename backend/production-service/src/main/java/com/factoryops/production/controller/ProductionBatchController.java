package com.factoryops.production.controller;

import com.factoryops.production.dto.request.ProductionBatchRequest;
import com.factoryops.production.dto.response.ApiResponse;
import com.factoryops.production.dto.response.ProductionBatchResponse;
import com.factoryops.production.service.ProductionBatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/production-batches")
@RequiredArgsConstructor
public class ProductionBatchController {

    private final ProductionBatchService productionBatchService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProductionBatchResponse>> create(@Valid @RequestBody ProductionBatchRequest request) {
        ProductionBatchResponse response = productionBatchService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "ProductionBatch created successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductionBatchResponse>> getById(@PathVariable Long id) {
        ProductionBatchResponse response = productionBatchService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "ProductionBatch retrieved successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductionBatchResponse>>> getAll() {
        List<ProductionBatchResponse> response = productionBatchService.getAll();
        return ResponseEntity.ok(ApiResponse.success(response, "ProductionBatch list retrieved successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductionBatchResponse>> update(@PathVariable Long id,
                                                             @Valid @RequestBody ProductionBatchRequest request) {
        ProductionBatchResponse response = productionBatchService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "ProductionBatch updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        productionBatchService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "ProductionBatch deleted successfully"));
    }
}
