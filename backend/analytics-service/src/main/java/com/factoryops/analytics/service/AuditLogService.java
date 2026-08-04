package com.factoryops.analytics.service;

import com.factoryops.analytics.dto.request.AuditLogRequest;
import com.factoryops.analytics.dto.response.AuditLogResponse;

import java.util.List;

public interface AuditLogService {

    AuditLogResponse create(AuditLogRequest request);

    AuditLogResponse getById(Long id);

    List<AuditLogResponse> getAll();

    AuditLogResponse update(Long id, AuditLogRequest request);

    void delete(Long id);
}
