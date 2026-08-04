package com.factoryops.analytics.service.impl;

import com.factoryops.analytics.dto.request.AuditLogRequest;
import com.factoryops.analytics.dto.response.AuditLogResponse;
import com.factoryops.analytics.entity.AuditLog;
import com.factoryops.analytics.exception.BusinessException;
import com.factoryops.analytics.exception.ResourceNotFoundException;
import com.factoryops.analytics.mapper.AuditLogMapper;
import com.factoryops.analytics.repository.AuditLogRepository;
import com.factoryops.analytics.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private static final String RESOURCE_NAME = "AuditLog";

    private final AuditLogRepository auditLogRepository;
    private final AuditLogMapper auditLogMapper;

    @Override
    public AuditLogResponse create(AuditLogRequest request) {

        validateRequest(request);

        AuditLog auditLog = auditLogMapper.toEntity(request);

        LocalDateTime now = LocalDateTime.now();

        if (auditLog.getPerformedAt() == null) {
            auditLog.setPerformedAt(now);
        }

        auditLog.setCreatedAt(now);
        auditLog.setUpdatedAt(now);

        try {
            AuditLog saved = auditLogRepository.save(auditLog);
            return auditLogMapper.toResponse(saved);
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessException("Unable to create audit log.");
        }
    }

    @Override
    public AuditLogResponse getById(Long id) {
        return auditLogMapper.toResponse(findAuditLogOrThrow(id));
    }

    @Override
    public List<AuditLogResponse> getAll() {
        return auditLogMapper.toResponseList(auditLogRepository.findAll());
    }

    @Override
    public AuditLogResponse update(Long id, AuditLogRequest request) {

        validateRequest(request);

        AuditLog auditLog = findAuditLogOrThrow(id);

        if (!request.getEntityName().equals(auditLog.getEntityName())
                || !request.getEntityId().equals(auditLog.getEntityId())
                || request.getAction() != auditLog.getAction()) {

            throw new BusinessException(
                    "Entity Name, Entity Id and Action cannot be modified once an audit log is created.");
        }

        auditLog.setPerformedBy(request.getPerformedBy());
        auditLog.setPerformedAt(request.getPerformedAt());
        auditLog.setDetails(request.getDetails());
        auditLog.setUpdatedAt(LocalDateTime.now());

        try {
            AuditLog updated = auditLogRepository.save(auditLog);
            return auditLogMapper.toResponse(updated);
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessException("Unable to update audit log.");
        }
    }

    @Override
    public void delete(Long id) {

        AuditLog auditLog = findAuditLogOrThrow(id);

        try {
            auditLogRepository.delete(auditLog);
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessException(
                    "Cannot delete audit log with id " + id + " because it is referenced by other records.");
        }
    }

    private void validateRequest(AuditLogRequest request) {

        if (request.getPerformedAt() != null
                && request.getPerformedAt().isAfter(LocalDateTime.now().plusMinutes(1))) {
            throw new BusinessException("Performed time cannot be in the future.");
        }

        if (request.getPerformedBy() != null
                && request.getPerformedBy().isBlank()) {
            throw new BusinessException("Performed By cannot be blank.");
        }
    }

    private AuditLog findAuditLogOrThrow(Long id) {

        return auditLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, id));
    }
}