package com.factoryops.quality.service.impl;

import com.factoryops.quality.dto.request.QualityInspectionRequest;
import com.factoryops.quality.dto.response.QualityInspectionResponse;
import com.factoryops.quality.entity.QualityInspection;
import com.factoryops.quality.exception.ResourceNotFoundException;
import com.factoryops.quality.mapper.QualityInspectionMapper;
import com.factoryops.quality.repository.QualityInspectionRepository;
import com.factoryops.quality.service.QualityInspectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class QualityInspectionServiceImpl implements QualityInspectionService {

    private static final String RESOURCE_NAME = "QualityInspection";

    private final QualityInspectionRepository qualityInspectionRepository;
    private final QualityInspectionMapper qualityInspectionMapper;

    @Transactional
    @Override
    public QualityInspectionResponse create(QualityInspectionRequest request) {

        log.info("Creating quality inspection");

        QualityInspection inspection =
                qualityInspectionMapper.toEntity(request);

        LocalDateTime now = LocalDateTime.now();

        if (inspection.getInspectionDate() == null) {
            inspection.setInspectionDate(now);
        }

        inspection.setCreatedAt(now);
        inspection.setUpdatedAt(now);

        QualityInspection saved =
                qualityInspectionRepository.save(inspection);

        log.info("Quality inspection created successfully : {}",
                saved.getId());

        return qualityInspectionMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public QualityInspectionResponse getById(Long id) {

        log.debug("Fetching quality inspection {}", id);

        QualityInspection inspection = findInspectionOrThrow(id);

        return qualityInspectionMapper.toResponse(inspection);
    }

    @Transactional(readOnly = true)
    @Override
    public List<QualityInspectionResponse> getAll() {

        log.debug("Fetching all quality inspections");

        return qualityInspectionMapper.toResponseList(
                qualityInspectionRepository.findAll()
        );
    }

    @Transactional
    @Override
    public QualityInspectionResponse update(Long id,
                                            QualityInspectionRequest request) {

        log.info("Updating quality inspection {}", id);

        QualityInspection inspection =
                findInspectionOrThrow(id);

        inspection.setInspectorName(request.getInspectorName());
        inspection.setInspectionDate(request.getInspectionDate());
        inspection.setResult(request.getResult());
        inspection.setRemarks(request.getRemarks());
        inspection.setProductionBatchId(request.getProductionBatchId());
        inspection.setUpdatedAt(LocalDateTime.now());
        QualityInspection updated =
                qualityInspectionRepository.save(inspection);

        log.info("Quality inspection updated successfully : {}",
                updated.getId());

        return qualityInspectionMapper.toResponse(updated);
    }

    @Transactional
    @Override
    public void delete(Long id) {

        log.info("Deleting quality inspection {}", id);

        QualityInspection inspection =
                findInspectionOrThrow(id);

        qualityInspectionRepository.delete(inspection);

        log.info("Quality inspection deleted successfully : {}", id);
    }

    private QualityInspection findInspectionOrThrow(Long id) {

        return qualityInspectionRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                RESOURCE_NAME,
                                id
                        ));
    }
}