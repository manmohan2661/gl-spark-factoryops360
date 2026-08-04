package com.factoryops.analytics.controller;

import com.factoryops.analytics.dto.request.NotificationRequest;
import com.factoryops.analytics.dto.response.ApiResponse;
import com.factoryops.analytics.dto.response.NotificationResponse;
import com.factoryops.analytics.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<ApiResponse<NotificationResponse>> create(@Valid @RequestBody NotificationRequest request) {
        NotificationResponse response = notificationService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Notification created successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<NotificationResponse>> getById(@PathVariable Long id) {
        NotificationResponse response = notificationService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Notification retrieved successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getAll() {
        List<NotificationResponse> response = notificationService.getAll();
        return ResponseEntity.ok(ApiResponse.success(response, "Notification list retrieved successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<NotificationResponse>> update(@PathVariable Long id,
                                                             @Valid @RequestBody NotificationRequest request) {
        NotificationResponse response = notificationService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Notification updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        notificationService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Notification deleted successfully"));
    }
}
