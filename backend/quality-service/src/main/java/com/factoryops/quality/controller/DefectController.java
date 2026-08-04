package com.factoryops.quality.controller;

import com.factoryops.quality.dto.request.DefectRequest;
import com.factoryops.quality.dto.response.ApiResponse;
import com.factoryops.quality.dto.response.DefectResponse;
import com.factoryops.quality.service.DefectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/defects")
@RequiredArgsConstructor
public class DefectController {

    private final DefectService defectService;

    @PostMapping
    public ResponseEntity<ApiResponse<DefectResponse>> create(@Valid @RequestBody DefectRequest request) {
        DefectResponse response = defectService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Defect created successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DefectResponse>> getById(@PathVariable Long id) {
        DefectResponse response = defectService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Defect retrieved successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DefectResponse>>> getAll() {
        List<DefectResponse> response = defectService.getAll();
        return ResponseEntity.ok(ApiResponse.success(response, "Defect list retrieved successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DefectResponse>> update(@PathVariable Long id,
                                                             @Valid @RequestBody DefectRequest request) {
        DefectResponse response = defectService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Defect updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        defectService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Defect deleted successfully"));
    }
}
