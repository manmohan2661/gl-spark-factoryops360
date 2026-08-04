package com.factoryops.analytics.service.impl;

import com.factoryops.analytics.dto.request.AlertRequest;
import com.factoryops.analytics.dto.response.AlertResponse;
import com.factoryops.analytics.entity.Alert;
import com.factoryops.analytics.exception.BusinessException;
import com.factoryops.analytics.exception.ResourceNotFoundException;
import com.factoryops.analytics.mapper.AlertMapper;
import com.factoryops.analytics.repository.AlertRepository;
import com.factoryops.analytics.service.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlertServiceImpl implements AlertService {

    private static final String RESOURCE_NAME = "Alert";

    private final AlertRepository alertRepository;
    private final AlertMapper alertMapper;

    @Override
    public AlertResponse create(AlertRequest request) {

        validateRequest(request);

        Alert alert = alertMapper.toEntity(request);

        LocalDateTime now = LocalDateTime.now();

        if (alert.getTriggeredAt() == null) {
            alert.setTriggeredAt(now);
        }

        if (alert.getAcknowledged() == null) {
            alert.setAcknowledged(false);
        }

        alert.setCreatedAt(now);
        alert.setUpdatedAt(now);

        try {
            Alert saved = alertRepository.save(alert);
            return alertMapper.toResponse(saved);
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessException("Unable to create alert.");
        }
    }

    @Override
    public AlertResponse getById(Long id) {
        return alertMapper.toResponse(findAlertOrThrow(id));
    }

    @Override
    public List<AlertResponse> getAll() {
        return alertMapper.toResponseList(alertRepository.findAll());
    }

    @Override
    public AlertResponse update(Long id, AlertRequest request) {

        validateRequest(request);

        Alert alert = findAlertOrThrow(id);

        alert.setTitle(request.getTitle());
        alert.setMessage(request.getMessage());
        alert.setSeverity(request.getSeverity());
        alert.setSourceService(request.getSourceService());
        alert.setTriggeredAt(request.getTriggeredAt());
        alert.setAcknowledged(request.getAcknowledged());
        alert.setAcknowledgedBy(request.getAcknowledgedBy());
        alert.setUpdatedAt(LocalDateTime.now());

        try {
            Alert updated = alertRepository.save(alert);
            return alertMapper.toResponse(updated);
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessException("Unable to update alert.");
        }
    }

    @Override
    public void delete(Long id) {

        Alert alert = findAlertOrThrow(id);

        try {
            alertRepository.delete(alert);
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessException(
                    "Cannot delete alert with id " + id + " because it is referenced by other records");
        }
    }

    private void validateRequest(AlertRequest request) {

        if (Boolean.TRUE.equals(request.getAcknowledged())
                && (request.getAcknowledgedBy() == null || request.getAcknowledgedBy().isBlank())) {
            throw new BusinessException("Acknowledged By is required when alert is acknowledged.");
        }

        if (request.getTriggeredAt() != null
                && request.getTriggeredAt().isAfter(LocalDateTime.now().plusMinutes(1))) {
            throw new BusinessException("Triggered time cannot be in the future.");
        }
    }

    private Alert findAlertOrThrow(Long id) {
        return alertRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, id));
    }
}