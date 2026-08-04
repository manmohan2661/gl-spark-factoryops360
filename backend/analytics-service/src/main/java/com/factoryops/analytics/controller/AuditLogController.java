package com.factoryops.analytics.controller;

import com.factoryops.analytics.dto.request.AuditLogRequest;
import com.factoryops.analytics.dto.response.ApiResponse;
import com.factoryops.analytics.dto.response.AuditLogResponse;
import com.factoryops.analytics.service.AuditLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    @PostMapping
    public ResponseEntity<ApiResponse<AuditLogResponse>> create(@Valid @RequestBody AuditLogRequest request) {
        AuditLogResponse response = auditLogService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "AuditLog created successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AuditLogResponse>> getById(@PathVariable Long id) {
        AuditLogResponse response = auditLogService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "AuditLog retrieved successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AuditLogResponse>>> getAll() {
        List<AuditLogResponse> response = auditLogService.getAll();
        return ResponseEntity.ok(ApiResponse.success(response, "AuditLog list retrieved successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AuditLogResponse>> update(@PathVariable Long id,
                                                             @Valid @RequestBody AuditLogRequest request) {
        AuditLogResponse response = auditLogService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "AuditLog updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        auditLogService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "AuditLog deleted successfully"));
    }
}
