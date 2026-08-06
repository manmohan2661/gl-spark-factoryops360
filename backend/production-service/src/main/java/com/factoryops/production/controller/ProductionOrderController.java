package com.factoryops.production.controller;

import com.factoryops.production.dto.request.ProductionOrderRequest;
import com.factoryops.production.dto.response.ApiResponse;
import com.factoryops.production.dto.response.ProductionOrderResponse;
import com.factoryops.production.service.ProductionOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/production-orders")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN') or hasRole('PRODUCTION_MANAGER')")
public class ProductionOrderController {

    private final ProductionOrderService productionOrderService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProductionOrderResponse>> create(@Valid @RequestBody ProductionOrderRequest request) {
        ProductionOrderResponse response = productionOrderService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "ProductionOrder created successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductionOrderResponse>> getById(@PathVariable Long id) {
        ProductionOrderResponse response = productionOrderService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "ProductionOrder retrieved successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductionOrderResponse>>> getAll() {
        List<ProductionOrderResponse> response = productionOrderService.getAll();
        return ResponseEntity.ok(ApiResponse.success(response, "ProductionOrder list retrieved successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductionOrderResponse>> update(@PathVariable Long id,
                                                             @Valid @RequestBody ProductionOrderRequest request) {
        ProductionOrderResponse response = productionOrderService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "ProductionOrder updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        productionOrderService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "ProductionOrder deleted successfully"));
    }
}
