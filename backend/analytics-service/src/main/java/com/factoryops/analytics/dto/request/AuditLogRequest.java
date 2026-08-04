package com.factoryops.analytics.dto.request;

import com.factoryops.analytics.entity.AuditAction;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogRequest {

    @NotBlank(message = "Entity name is required")
    @Size(max = 100, message = "Entity name cannot exceed 100 characters")
    private String entityName;

    @NotNull(message = "Entity id is required")
    private Long entityId;

    @NotNull(message = "Action is required")
    private AuditAction action;

    @Size(max = 100, message = "Performed By cannot exceed 100 characters")
    private String performedBy;

    private LocalDateTime performedAt;

    @Size(max = 1000, message = "Details cannot exceed 1000 characters")
    private String details;
}