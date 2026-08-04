package com.factoryops.analytics.mapper;

import com.factoryops.analytics.dto.request.AuditLogRequest;
import com.factoryops.analytics.dto.response.AuditLogResponse;
import com.factoryops.analytics.entity.AuditLog;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AuditLogMapperImpl implements AuditLogMapper {

    @Override
    public AuditLog toEntity(AuditLogRequest request) {
        if (request == null) {
            return null;
        }
        return AuditLog.builder()
                .entityName(request.getEntityName())
                .entityId(request.getEntityId())
                .action(request.getAction())
                .performedBy(request.getPerformedBy())
                .performedAt(request.getPerformedAt())
                .details(request.getDetails())
                .build();
    }

    @Override
    public AuditLogResponse toResponse(AuditLog entity) {
        if (entity == null) {
            return null;
        }
        return AuditLogResponse.builder()
                .id(entity.getId())
                .entityName(entity.getEntityName())
                .entityId(entity.getEntityId())
                .action(entity.getAction())
                .performedBy(entity.getPerformedBy())
                .performedAt(entity.getPerformedAt())
                .details(entity.getDetails())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    @Override
    public List<AuditLogResponse> toResponseList(List<AuditLog> entities) {
        if (entities == null) {
            return List.of();
        }
        return entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
