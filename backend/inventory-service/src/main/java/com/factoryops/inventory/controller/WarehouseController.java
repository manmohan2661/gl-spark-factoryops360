package com.factoryops.inventory.controller;

import com.factoryops.inventory.dto.request.WarehouseRequest;
import com.factoryops.inventory.dto.response.ApiResponse;
import com.factoryops.inventory.dto.response.WarehouseResponse;
import com.factoryops.inventory.service.WarehouseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/warehouses")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    @PostMapping
    public ResponseEntity<ApiResponse<WarehouseResponse>> create(@Valid @RequestBody WarehouseRequest request) {
        WarehouseResponse response = warehouseService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Warehouse created successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<WarehouseResponse>> getById(@PathVariable Long id) {
        WarehouseResponse response = warehouseService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Warehouse retrieved successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<WarehouseResponse>>> getAll() {
        List<WarehouseResponse> response = warehouseService.getAll();
        return ResponseEntity.ok(ApiResponse.success(response, "Warehouse list retrieved successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<WarehouseResponse>> update(@PathVariable Long id,
                                                             @Valid @RequestBody WarehouseRequest request) {
        WarehouseResponse response = warehouseService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Warehouse updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        warehouseService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Warehouse deleted successfully"));
    }
}
