package com.factoryops.supplier.mapper;

import com.factoryops.supplier.dto.request.SupplierPerformanceRequest;
import com.factoryops.supplier.dto.response.SupplierPerformanceResponse;
import com.factoryops.supplier.entity.SupplierPerformance;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SupplierPerformanceMapperImpl implements SupplierPerformanceMapper {

    @Override
    public SupplierPerformance toEntity(SupplierPerformanceRequest request) {
        if (request == null) {
            return null;
        }
        // supplier association is resolved and set by the service layer
        // (this mapper has no repository access)
        return SupplierPerformance.builder()
                .evaluationPeriod(request.getEvaluationPeriod())
                .onTimeDeliveryRate(request.getOnTimeDeliveryRate())
                .qualityScore(request.getQualityScore())
                .defectRate(request.getDefectRate())
                .remarks(request.getRemarks())
                .evaluatedAt(request.getEvaluatedAt())
                .build();
    }

    @Override
    public SupplierPerformanceResponse toResponse(SupplierPerformance entity) {
        if (entity == null) {
            return null;
        }
        return SupplierPerformanceResponse.builder()
                .id(entity.getId())
                .evaluationPeriod(entity.getEvaluationPeriod())
                .onTimeDeliveryRate(entity.getOnTimeDeliveryRate())
                .qualityScore(entity.getQualityScore())
                .defectRate(entity.getDefectRate())
                .remarks(entity.getRemarks())
                .evaluatedAt(entity.getEvaluatedAt())
                .supplierId(entity.getSupplier() != null ? entity.getSupplier().getId() : null)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    @Override
    public List<SupplierPerformanceResponse> toResponseList(List<SupplierPerformance> entities) {
        if (entities == null) {
            return List.of();
        }
        return entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
