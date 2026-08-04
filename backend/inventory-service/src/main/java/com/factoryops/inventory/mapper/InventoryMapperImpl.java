package com.factoryops.inventory.mapper;

import com.factoryops.inventory.dto.request.InventoryRequest;
import com.factoryops.inventory.dto.response.InventoryResponse;
import com.factoryops.inventory.entity.Inventory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class InventoryMapperImpl implements InventoryMapper {

    @Override
    public Inventory toEntity(InventoryRequest request) {

        if (request == null) {
            return null;
        }

        // Material aur Warehouse associations Service layer set karegi
        return Inventory.builder()
                .quantityAvailable(request.getQuantityAvailable())
                .quantityReserved(
                        request.getQuantityReserved() == null
                                ? 0
                                : request.getQuantityReserved()
                )
                .build();
    }

    @Override
    public InventoryResponse toResponse(Inventory entity) {

        if (entity == null) {
            return null;
        }

        return InventoryResponse.builder()
                .id(entity.getId())
                .quantityAvailable(entity.getQuantityAvailable())
                .quantityReserved(entity.getQuantityReserved())
                .lastUpdated(entity.getLastUpdated())
                .materialId(
                        entity.getMaterial() != null
                                ? entity.getMaterial().getId()
                                : null
                )
                .warehouseId(
                        entity.getWarehouse() != null
                                ? entity.getWarehouse().getId()
                                : null
                )
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    @Override
    public List<InventoryResponse> toResponseList(List<Inventory> entities) {

        if (entities == null || entities.isEmpty()) {
            return List.of();
        }

        return entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}