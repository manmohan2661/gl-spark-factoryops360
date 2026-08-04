package com.factoryops.production.mapper;

import com.factoryops.production.dto.request.ProductionBatchRequest;
import com.factoryops.production.dto.response.ProductionBatchResponse;
import com.factoryops.production.entity.ProductionBatch;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductionBatchMapperImpl implements ProductionBatchMapper {

    @Override
    public ProductionBatch toEntity(ProductionBatchRequest request) {

        if (request == null) {
            return null;
        }

        return ProductionBatch.builder()
                .batchNumber(request.getBatchNumber())
                .quantity(request.getQuantity())
                .status(request.getStatus())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .build();
    }

    @Override
    public ProductionBatchResponse toResponse(ProductionBatch entity) {

        if (entity == null) {
            return null;
        }

        return ProductionBatchResponse.builder()
                .id(entity.getId())
                .batchNumber(entity.getBatchNumber())
                .quantity(entity.getQuantity())
                .status(entity.getStatus())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .productionOrderId(
                        entity.getProductionOrder() != null
                                ? entity.getProductionOrder().getId()
                                : null
                )
                .machineId(
                        entity.getMachine() != null
                                ? entity.getMachine().getId()
                                : null
                )
                .shiftId(
                        entity.getShift() != null
                                ? entity.getShift().getId()
                                : null
                )
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    @Override
    public List<ProductionBatchResponse> toResponseList(List<ProductionBatch> entities) {

        if (entities == null) {
            return List.of();
        }

        return entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}