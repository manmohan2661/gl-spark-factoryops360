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
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DefectServiceImpl implements DefectService {

    private static final String RESOURCE_NAME = "Defect";

    private final DefectRepository defectRepository;
    private final DefectMapper defectMapper;
    private final QualityInspectionRepository qualityInspectionRepository;

    @Override
    public DefectResponse create(DefectRequest request) {
        QualityInspection inspection = findInspectionOrThrow(request.getQualityInspectionId());

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

        // A logged defect means the unit did not actually pass inspection;
        // keep the parent inspection's result consistent with reality.
        if (inspection.getResult() == InspectionResult.PASS) {
            inspection.setResult(InspectionResult.FAIL);
            inspection.setUpdatedAt(now);
            qualityInspectionRepository.save(inspection);
        }

        Defect saved = defectRepository.save(defect);
        return defectMapper.toResponse(saved);
    }

    @Override
    public DefectResponse getById(Long id) {
        Defect defect = findDefectOrThrow(id);
        return defectMapper.toResponse(defect);
    }

    @Override
    public List<DefectResponse> getAll() {
        return defectMapper.toResponseList(defectRepository.findAll());
    }

    @Override
    public DefectResponse update(Long id, DefectRequest request) {
        Defect defect = findDefectOrThrow(id);
        QualityInspection inspection = findInspectionOrThrow(request.getQualityInspectionId());

        defect.setDefectType(request.getDefectType());
        defect.setSeverity(request.getSeverity());
        defect.setDescription(request.getDescription());
        defect.setReportedDate(request.getReportedDate());
        defect.setResolved(request.getResolved());
        defect.setQualityInspection(inspection);
        defect.setUpdatedAt(LocalDateTime.now());

        Defect updated = defectRepository.save(defect);
        return defectMapper.toResponse(updated);
    }

    @Override
    public void delete(Long id) {
        Defect defect = findDefectOrThrow(id);
        defectRepository.delete(defect);
    }

    private Defect findDefectOrThrow(Long id) {
        return defectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, id));
    }

    private QualityInspection findInspectionOrThrow(Long id) {
        return qualityInspectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("QualityInspection", id));
    }
}
