package com.factoryops.inventory.controller;

import com.factoryops.inventory.dto.request.InventoryTransactionRequest;
import com.factoryops.inventory.dto.response.ApiResponse;
import com.factoryops.inventory.dto.response.InventoryTransactionResponse;
import com.factoryops.inventory.service.InventoryTransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventory-transactions")
@RequiredArgsConstructor
public class InventoryTransactionController {

    private final InventoryTransactionService inventoryTransactionService;

    @PostMapping
    public ResponseEntity<ApiResponse<InventoryTransactionResponse>> create(@Valid @RequestBody InventoryTransactionRequest request) {
        InventoryTransactionResponse response = inventoryTransactionService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "InventoryTransaction created successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InventoryTransactionResponse>> getById(@PathVariable Long id) {
        InventoryTransactionResponse response = inventoryTransactionService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "InventoryTransaction retrieved successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<InventoryTransactionResponse>>> getAll() {
        List<InventoryTransactionResponse> response = inventoryTransactionService.getAll();
        return ResponseEntity.ok(ApiResponse.success(response, "InventoryTransaction list retrieved successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<InventoryTransactionResponse>> update(@PathVariable Long id,
                                                             @Valid @RequestBody InventoryTransactionRequest request) {
        InventoryTransactionResponse response = inventoryTransactionService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "InventoryTransaction updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        inventoryTransactionService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "InventoryTransaction deleted successfully"));
    }
}
