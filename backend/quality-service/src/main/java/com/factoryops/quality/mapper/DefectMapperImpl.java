package com.factoryops.quality.mapper;

import com.factoryops.quality.dto.request.DefectRequest;
import com.factoryops.quality.dto.response.DefectResponse;
import com.factoryops.quality.entity.Defect;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DefectMapperImpl implements DefectMapper {

    @Override
    public Defect toEntity(DefectRequest request) {
        if (request == null) {
            return null;
        }
        // qualityInspection association is resolved and set by the service
        // layer (this mapper has no repository access)
        return Defect.builder()
                .defectType(request.getDefectType())
                .severity(request.getSeverity())
                .description(request.getDescription())
                .reportedDate(request.getReportedDate())
                .resolved(request.getResolved())
                .build();
    }

    @Override
    public DefectResponse toResponse(Defect entity) {
        if (entity == null) {
            return null;
        }
        return DefectResponse.builder()
                .id(entity.getId())
                .defectType(entity.getDefectType())
                .severity(entity.getSeverity())
                .description(entity.getDescription())
                .reportedDate(entity.getReportedDate())
                .resolved(entity.getResolved())
                .qualityInspectionId(
                        entity.getQualityInspection() != null ? entity.getQualityInspection().getId() : null)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    @Override
    public List<DefectResponse> toResponseList(List<Defect> entities) {
        if (entities == null) {
            return List.of();
        }
        return entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
