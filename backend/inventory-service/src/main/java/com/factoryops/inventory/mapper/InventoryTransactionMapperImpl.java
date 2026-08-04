package com.factoryops.inventory.mapper;

import com.factoryops.inventory.dto.request.InventoryTransactionRequest;
import com.factoryops.inventory.dto.response.InventoryTransactionResponse;
import com.factoryops.inventory.entity.InventoryTransaction;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class InventoryTransactionMapperImpl implements InventoryTransactionMapper {

    @Override
    public InventoryTransaction toEntity(InventoryTransactionRequest request) {

        if (request == null) {
            return null;
        }

        // Inventory association Service layer set karegi
        // Transaction Date bhi Service layer set karegi

        return InventoryTransaction.builder()
                .transactionType(request.getTransactionType())
                .quantity(request.getQuantity())
                .referenceNumber(request.getReferenceNumber())
                .remarks(request.getRemarks())
                .build();
    }

    @Override
    public InventoryTransactionResponse toResponse(InventoryTransaction entity) {

        if (entity == null) {
            return null;
        }

        return InventoryTransactionResponse.builder()
                .id(entity.getId())
                .transactionType(entity.getTransactionType())
                .quantity(entity.getQuantity())
                .referenceNumber(entity.getReferenceNumber())
                .transactionDate(entity.getTransactionDate())
                .remarks(entity.getRemarks())
                .inventoryId(
                        entity.getInventory() != null
                                ? entity.getInventory().getId()
                                : null
                )
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    @Override
    public List<InventoryTransactionResponse> toResponseList(List<InventoryTransaction> entities) {

        if (entities == null || entities.isEmpty()) {
            return List.of();
        }

        return entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}