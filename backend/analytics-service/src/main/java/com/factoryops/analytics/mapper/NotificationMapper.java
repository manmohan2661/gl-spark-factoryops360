package com.factoryops.analytics.mapper;

import com.factoryops.analytics.dto.request.NotificationRequest;
import com.factoryops.analytics.dto.response.NotificationResponse;
import com.factoryops.analytics.entity.Notification;

import java.util.List;

// Plain mapper contract. MapStruct is not yet a declared dependency of this
// module's pom.xml; once added, this interface can be annotated with
// @Mapper(componentModel = "spring") and left otherwise unchanged.
public interface NotificationMapper {

    Notification toEntity(NotificationRequest request);

    NotificationResponse toResponse(Notification entity);

    List<NotificationResponse> toResponseList(List<Notification> entities);
}
