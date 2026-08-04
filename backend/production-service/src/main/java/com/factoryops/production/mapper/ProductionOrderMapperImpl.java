package com.factoryops.production.mapper;

import com.factoryops.production.dto.request.ProductionOrderRequest;
import com.factoryops.production.dto.response.ProductionOrderResponse;
import com.factoryops.production.entity.ProductionOrder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductionOrderMapperImpl implements ProductionOrderMapper {

    @Override
    public ProductionOrder toEntity(ProductionOrderRequest request) {

        if (request == null) {
            return null;
        }

        return ProductionOrder.builder()
                .orderNumber(request.getOrderNumber())
                .productName(request.getProductName())
                .quantityOrdered(request.getQuantityOrdered())
                .quantityProduced(request.getQuantityProduced())
                .status(request.getStatus())
                .priority(request.getPriority())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();
    }

    @Override
    public ProductionOrderResponse toResponse(ProductionOrder entity) {

        if (entity == null) {
            return null;
        }

        return ProductionOrderResponse.builder()
                .id(entity.getId())
                .orderNumber(entity.getOrderNumber())
                .productName(entity.getProductName())
                .quantityOrdered(entity.getQuantityOrdered())
                .quantityProduced(entity.getQuantityProduced())
                .status(entity.getStatus())
                .priority(entity.getPriority())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    @Override
    public List<ProductionOrderResponse> toResponseList(List<ProductionOrder> entities) {

        if (entities == null) {
            return List.of();
        }

        return entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}