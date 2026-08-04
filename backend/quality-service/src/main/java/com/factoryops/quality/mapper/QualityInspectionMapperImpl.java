package com.factoryops.quality.mapper;

import com.factoryops.quality.dto.request.QualityInspectionRequest;
import com.factoryops.quality.dto.response.QualityInspectionResponse;
import com.factoryops.quality.entity.QualityInspection;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class QualityInspectionMapperImpl implements QualityInspectionMapper {

    @Override
    public QualityInspection toEntity(QualityInspectionRequest request) {
        if (request == null) {
            return null;
        }
        return QualityInspection.builder()
                .inspectorName(request.getInspectorName())
                .inspectionDate(request.getInspectionDate())
                .result(request.getResult())
                .remarks(request.getRemarks())
                .productionBatchId(request.getProductionBatchId())
                .build();
    }

    @Override
    public QualityInspectionResponse toResponse(QualityInspection entity) {
        if (entity == null) {
            return null;
        }
        return QualityInspectionResponse.builder()
                .id(entity.getId())
                .inspectorName(entity.getInspectorName())
                .inspectionDate(entity.getInspectionDate())
                .result(entity.getResult())
                .remarks(entity.getRemarks())
                .productionBatchId(entity.getProductionBatchId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    @Override
    public List<QualityInspectionResponse> toResponseList(List<QualityInspection> entities) {
        if (entities == null) {
            return List.of();
        }
        return entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
