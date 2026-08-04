package com.factoryops.analytics.mapper;

import com.factoryops.analytics.dto.request.NotificationRequest;
import com.factoryops.analytics.dto.response.NotificationResponse;
import com.factoryops.analytics.entity.Notification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class NotificationMapperImpl implements NotificationMapper {

    @Override
    public Notification toEntity(NotificationRequest request) {
        if (request == null) {
            return null;
        }
        return Notification.builder()
                .recipient(request.getRecipient())
                .title(request.getTitle())
                .message(request.getMessage())
                .channel(request.getChannel())
                .status(request.getStatus())
                .sentAt(request.getSentAt())
                .build();
    }

    @Override
    public NotificationResponse toResponse(Notification entity) {
        if (entity == null) {
            return null;
        }
        return NotificationResponse.builder()
                .id(entity.getId())
                .recipient(entity.getRecipient())
                .title(entity.getTitle())
                .message(entity.getMessage())
                .channel(entity.getChannel())
                .status(entity.getStatus())
                .sentAt(entity.getSentAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    @Override
    public List<NotificationResponse> toResponseList(List<Notification> entities) {
        if (entities == null) {
            return List.of();
        }
        return entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
