package com.factoryops.production.controller;

import com.factoryops.production.dto.request.MachineRequest;
import com.factoryops.production.dto.response.ApiResponse;
import com.factoryops.production.dto.response.MachineResponse;
import com.factoryops.production.service.MachineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/machines")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN') or hasRole('PRODUCTION_MANAGER')")
public class MachineController {

    private final MachineService machineService;

    @PostMapping
    public ResponseEntity<ApiResponse<MachineResponse>> create(@Valid @RequestBody MachineRequest request) {
        MachineResponse response = machineService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Machine created successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MachineResponse>> getById(@PathVariable Long id) {
        MachineResponse response = machineService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Machine retrieved successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MachineResponse>>> getAll() {
        List<MachineResponse> response = machineService.getAll();
        return ResponseEntity.ok(ApiResponse.success(response, "Machine list retrieved successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MachineResponse>> update(@PathVariable Long id,
                                                             @Valid @RequestBody MachineRequest request) {
        MachineResponse response = machineService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Machine updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        machineService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Machine deleted successfully"));
    }
}
