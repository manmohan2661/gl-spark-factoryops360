package com.factoryops.analytics.mapper;

import com.factoryops.analytics.dto.request.AlertRequest;
import com.factoryops.analytics.dto.response.AlertResponse;
import com.factoryops.analytics.entity.Alert;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AlertMapperImpl implements AlertMapper {

    @Override
    public Alert toEntity(AlertRequest request) {
        if (request == null) {
            return null;
        }
        return Alert.builder()
                .title(request.getTitle())
                .message(request.getMessage())
                .severity(request.getSeverity())
                .sourceService(request.getSourceService())
                .triggeredAt(request.getTriggeredAt())
                .acknowledged(request.getAcknowledged())
                .acknowledgedBy(request.getAcknowledgedBy())
                .build();
    }

    @Override
    public AlertResponse toResponse(Alert entity) {
        if (entity == null) {
            return null;
        }
        return AlertResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .message(entity.getMessage())
                .severity(entity.getSeverity())
                .sourceService(entity.getSourceService())
                .triggeredAt(entity.getTriggeredAt())
                .acknowledged(entity.getAcknowledged())
                .acknowledgedBy(entity.getAcknowledgedBy())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    @Override
    public List<AlertResponse> toResponseList(List<Alert> entities) {
        if (entities == null) {
            return List.of();
        }
        return entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
