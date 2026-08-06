package com.factoryops.inventory.controller;

import com.factoryops.inventory.dto.request.InventoryRequest;
import com.factoryops.inventory.dto.response.ApiResponse;
import com.factoryops.inventory.dto.response.InventoryResponse;
import com.factoryops.inventory.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventories")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN') or hasRole('INVENTORY_MANAGER')")
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping
    public ResponseEntity<ApiResponse<InventoryResponse>> create(@Valid @RequestBody InventoryRequest request) {
        InventoryResponse response = inventoryService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Inventory created successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InventoryResponse>> getById(@PathVariable Long id) {
        InventoryResponse response = inventoryService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Inventory retrieved successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<InventoryResponse>>> getAll() {
        List<InventoryResponse> response = inventoryService.getAll();
        return ResponseEntity.ok(ApiResponse.success(response, "Inventory list retrieved successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<InventoryResponse>> update(@PathVariable Long id,
                                                             @Valid @RequestBody InventoryRequest request) {
        InventoryResponse response = inventoryService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Inventory updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        inventoryService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Inventory deleted successfully"));
    }
}
