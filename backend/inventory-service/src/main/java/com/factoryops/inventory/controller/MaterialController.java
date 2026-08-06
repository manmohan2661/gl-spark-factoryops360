package com.factoryops.inventory.controller;

import com.factoryops.inventory.dto.request.MaterialRequest;
import com.factoryops.inventory.dto.response.ApiResponse;
import com.factoryops.inventory.dto.response.MaterialResponse;
import com.factoryops.inventory.service.MaterialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/materials")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN') or hasRole('INVENTORY_MANAGER')")
public class MaterialController {

    private final MaterialService materialService;

    @PostMapping
    public ResponseEntity<ApiResponse<MaterialResponse>> create(@Valid @RequestBody MaterialRequest request) {
        MaterialResponse response = materialService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Material created successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MaterialResponse>> getById(@PathVariable Long id) {
        MaterialResponse response = materialService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Material retrieved successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MaterialResponse>>> getAll() {
        List<MaterialResponse> response = materialService.getAll();
        return ResponseEntity.ok(ApiResponse.success(response, "Material list retrieved successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MaterialResponse>> update(@PathVariable Long id,
                                                             @Valid @RequestBody MaterialRequest request) {
        MaterialResponse response = materialService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Material updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        materialService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Material deleted successfully"));
    }
}
