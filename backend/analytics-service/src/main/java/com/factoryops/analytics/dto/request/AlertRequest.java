package com.factoryops.analytics.dto.request;

import com.factoryops.analytics.entity.AlertSeverity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 150, message = "Title cannot exceed 150 characters")
    private String title;

    @NotBlank(message = "Message is required")
    private String message;

    @NotNull(message = "Severity is required")
    private AlertSeverity severity;

    @Size(max = 100, message = "Source service cannot exceed 100 characters")
    private String sourceService;

    private LocalDateTime triggeredAt;

    private Boolean acknowledged;

    @Size(max = 100, message = "Acknowledged By cannot exceed 100 characters")
    private String acknowledgedBy;
}