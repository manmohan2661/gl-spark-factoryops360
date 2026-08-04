package com.factoryops.quality.controller;

import com.factoryops.quality.dto.request.QualityInspectionRequest;
import com.factoryops.quality.dto.response.ApiResponse;
import com.factoryops.quality.dto.response.QualityInspectionResponse;
import com.factoryops.quality.service.QualityInspectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/quality-inspections")
@RequiredArgsConstructor
public class QualityInspectionController {

    private final QualityInspectionService qualityInspectionService;

    @PostMapping
    public ResponseEntity<ApiResponse<QualityInspectionResponse>> create(@Valid @RequestBody QualityInspectionRequest request) {
        QualityInspectionResponse response = qualityInspectionService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "QualityInspection created successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<QualityInspectionResponse>> getById(@PathVariable Long id) {
        QualityInspectionResponse response = qualityInspectionService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "QualityInspection retrieved successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<QualityInspectionResponse>>> getAll() {
        List<QualityInspectionResponse> response = qualityInspectionService.getAll();
        return ResponseEntity.ok(ApiResponse.success(response, "QualityInspection list retrieved successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<QualityInspectionResponse>> update(@PathVariable Long id,
                                                             @Valid @RequestBody QualityInspectionRequest request) {
        QualityInspectionResponse response = qualityInspectionService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "QualityInspection updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        qualityInspectionService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "QualityInspection deleted successfully"));
    }
}
