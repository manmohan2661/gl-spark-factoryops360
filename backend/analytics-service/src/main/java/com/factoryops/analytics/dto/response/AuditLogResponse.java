package com.factoryops.analytics.dto.response;

import com.factoryops.analytics.entity.AuditAction;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogResponse {

    private Long id;

    private String entityName;

    private Long entityId;

    private AuditAction action;

    private String performedBy;

    private LocalDateTime performedAt;

    private String details;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
