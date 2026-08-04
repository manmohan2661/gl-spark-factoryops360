package com.factoryops.analytics.service;

import com.factoryops.analytics.dto.request.NotificationRequest;
import com.factoryops.analytics.dto.response.NotificationResponse;

import java.util.List;

public interface NotificationService {

    NotificationResponse create(NotificationRequest request);

    NotificationResponse getById(Long id);

    List<NotificationResponse> getAll();

    NotificationResponse update(Long id, NotificationRequest request);

    void delete(Long id);
}
