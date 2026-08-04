package com.factoryops.quality.service.impl;

import com.factoryops.quality.dto.request.QualityInspectionRequest;
import com.factoryops.quality.dto.response.QualityInspectionResponse;
import com.factoryops.quality.entity.QualityInspection;
import com.factoryops.quality.exception.ResourceNotFoundException;
import com.factoryops.quality.mapper.QualityInspectionMapper;
import com.factoryops.quality.repository.QualityInspectionRepository;
import com.factoryops.quality.service.QualityInspectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QualityInspectionServiceImpl implements QualityInspectionService {

    private static final String RESOURCE_NAME = "QualityInspection";

    private final QualityInspectionRepository qualityInspectionRepository;
    private final QualityInspectionMapper qualityInspectionMapper;

    @Override
    public QualityInspectionResponse create(QualityInspectionRequest request) {
        QualityInspection inspection = qualityInspectionMapper.toEntity(request);

        LocalDateTime now = LocalDateTime.now();
        if (inspection.getInspectionDate() == null) {
            inspection.setInspectionDate(now);
        }
        inspection.setCreatedAt(now);
        inspection.setUpdatedAt(now);

        QualityInspection saved = qualityInspectionRepository.save(inspection);
        return qualityInspectionMapper.toResponse(saved);
    }

    @Override
    public QualityInspectionResponse getById(Long id) {
        QualityInspection inspection = findInspectionOrThrow(id);
        return qualityInspectionMapper.toResponse(inspection);
    }

    @Override
    public List<QualityInspectionResponse> getAll() {
        return qualityInspectionMapper.toResponseList(qualityInspectionRepository.findAll());
    }

    @Override
    public QualityInspectionResponse update(Long id, QualityInspectionRequest request) {
        QualityInspection inspection = findInspectionOrThrow(id);

        inspection.setInspectorName(request.getInspectorName());
        inspection.setInspectionDate(request.getInspectionDate());
        inspection.setResult(request.getResult());
        inspection.setRemarks(request.getRemarks());
        inspection.setProductionBatchId(request.getProductionBatchId());
        inspection.setUpdatedAt(LocalDateTime.now());

        QualityInspection updated = qualityInspectionRepository.save(inspection);
        return qualityInspectionMapper.toResponse(updated);
    }

    @Override
    public void delete(Long id) {
        QualityInspection inspection = findInspectionOrThrow(id);
        qualityInspectionRepository.delete(inspection);
    }

    private QualityInspection findInspectionOrThrow(Long id) {
        return qualityInspectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, id));
    }
}
