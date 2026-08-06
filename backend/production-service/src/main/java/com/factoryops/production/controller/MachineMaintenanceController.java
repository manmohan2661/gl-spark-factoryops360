package com.factoryops.production.controller;

import com.factoryops.production.dto.request.MachineMaintenanceRequest;
import com.factoryops.production.dto.response.ApiResponse;
import com.factoryops.production.dto.response.MachineMaintenanceResponse;
import com.factoryops.production.service.MachineMaintenanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/machine-maintenances")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN') or hasRole('PRODUCTION_MANAGER')")
public class MachineMaintenanceController {

    private final MachineMaintenanceService machineMaintenanceService;

    @PostMapping
    public ResponseEntity<ApiResponse<MachineMaintenanceResponse>> create(@Valid @RequestBody MachineMaintenanceRequest request) {
        MachineMaintenanceResponse response = machineMaintenanceService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "MachineMaintenance created successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MachineMaintenanceResponse>> getById(@PathVariable Long id) {
        MachineMaintenanceResponse response = machineMaintenanceService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "MachineMaintenance retrieved successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MachineMaintenanceResponse>>> getAll() {
        List<MachineMaintenanceResponse> response = machineMaintenanceService.getAll();
        return ResponseEntity.ok(ApiResponse.success(response, "MachineMaintenance list retrieved successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MachineMaintenanceResponse>> update(@PathVariable Long id,
                                                             @Valid @RequestBody MachineMaintenanceRequest request) {
        MachineMaintenanceResponse response = machineMaintenanceService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "MachineMaintenance updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        machineMaintenanceService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "MachineMaintenance deleted successfully"));
    }
}
