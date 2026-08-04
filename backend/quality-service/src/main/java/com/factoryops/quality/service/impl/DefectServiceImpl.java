package com.factoryops.quality.service.impl;

import com.factoryops.quality.dto.request.DefectRequest;
import com.factoryops.quality.dto.response.DefectResponse;
import com.factoryops.quality.entity.Defect;
import com.factoryops.quality.entity.InspectionResult;
import com.factoryops.quality.entity.QualityInspection;
import com.factoryops.quality.exception.ResourceNotFoundException;
import com.factoryops.quality.mapper.DefectMapper;
import com.factoryops.quality.repository.DefectRepository;
import com.factoryops.quality.repository.QualityInspectionRepository;
import com.factoryops.quality.service.DefectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefectServiceImpl implements DefectService {

    private static final String RESOURCE_NAME = "Defect";

    private final DefectRepository defectRepository;
    private final DefectMapper defectMapper;
    private final QualityInspectionRepository qualityInspectionRepository;

    @Transactional
    @Override
    public DefectResponse create(DefectRequest request) {

        log.info("Creating defect for inspection {}",
                request.getQualityInspectionId());

        QualityInspection inspection =
                findInspectionOrThrow(request.getQualityInspectionId());

        Defect defect = defectMapper.toEntity(request);

        defect.setQualityInspection(inspection);

        LocalDateTime now = LocalDateTime.now();

        if (defect.getReportedDate() == null) {
            defect.setReportedDate(now);
        }

        if (defect.getResolved() == null) {
            defect.setResolved(false);
        }

        defect.setCreatedAt(now);
        defect.setUpdatedAt(now);

        if (inspection.getResult() == InspectionResult.PASS) {

            inspection.setResult(InspectionResult.FAIL);
            inspection.setUpdatedAt(now);

            qualityInspectionRepository.save(inspection);
        }

        Defect saved = defectRepository.save(defect);

        log.info("Defect created successfully : {}",
                saved.getId());

        return defectMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public DefectResponse getById(Long id) {

        log.debug("Fetching defect {}", id);

        Defect defect = findDefectOrThrow(id);

        return defectMapper.toResponse(defect);
    }

    @Transactional(readOnly = true)
    @Override
    public List<DefectResponse> getAll() {

        log.debug("Fetching all defects");

        return defectMapper.toResponseList(
                defectRepository.findAll()
        );
    }

    @Transactional
    @Override
    public DefectResponse update(Long id,
                                 DefectRequest request) {

        log.info("Updating defect {}", id);

        Defect defect = findDefectOrThrow(id);

        QualityInspection inspection =
                findInspectionOrThrow(request.getQualityInspectionId());

        defect.setDefectType(request.getDefectType());
        defect.setSeverity(request.getSeverity());
        defect.setDescription(request.getDescription());
        defect.setReportedDate(request.getReportedDate());
        defect.setResolved(request.getResolved());
        defect.setQualityInspection(inspection);
        defect.setUpdatedAt(LocalDateTime.now());

        Defect updated = defectRepository.save(defect);

        log.info("Defect updated successfully : {}",
                updated.getId());

        return defectMapper.toResponse(updated);
    }

    @Transactional
    @Override
    public void delete(Long id) {

        log.info("Deleting defect {}", id);

        Defect defect = findDefectOrThrow(id);

        defectRepository.delete(defect);

        log.info("Defect deleted successfully : {}", id);
    }

    private Defect findDefectOrThrow(Long id) {

        return defectRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                RESOURCE_NAME,
                                id
                        ));
    }

    private QualityInspection findInspectionOrThrow(Long id) {

        return qualityInspectionRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "QualityInspection",
                                id
                        ));
    }
}