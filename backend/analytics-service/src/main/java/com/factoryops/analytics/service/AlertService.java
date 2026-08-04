package com.factoryops.analytics.service;

import com.factoryops.analytics.dto.request.AlertRequest;
import com.factoryops.analytics.dto.response.AlertResponse;

import java.util.List;

public interface AlertService {

    AlertResponse create(AlertRequest request);

    AlertResponse getById(Long id);

    List<AlertResponse> getAll();

    AlertResponse update(Long id, AlertRequest request);

    void delete(Long id);
}
