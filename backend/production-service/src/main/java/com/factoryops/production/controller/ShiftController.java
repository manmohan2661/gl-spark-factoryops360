package com.factoryops.production.controller;

import com.factoryops.production.dto.request.ShiftRequest;
import com.factoryops.production.dto.response.ApiResponse;
import com.factoryops.production.dto.response.ShiftResponse;
import com.factoryops.production.service.ShiftService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/shifts")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN') or hasRole('PRODUCTION_MANAGER')")
public class ShiftController {

    private final ShiftService shiftService;

    @PostMapping
    public ResponseEntity<ApiResponse<ShiftResponse>> create(@Valid @RequestBody ShiftRequest request) {
        ShiftResponse response = shiftService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Shift created successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ShiftResponse>> getById(@PathVariable Long id) {
        ShiftResponse response = shiftService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Shift retrieved successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ShiftResponse>>> getAll() {
        List<ShiftResponse> response = shiftService.getAll();
        return ResponseEntity.ok(ApiResponse.success(response, "Shift list retrieved successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ShiftResponse>> update(@PathVariable Long id,
                                                             @Valid @RequestBody ShiftRequest request) {
        ShiftResponse response = shiftService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Shift updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        shiftService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Shift deleted successfully"));
    }
}
