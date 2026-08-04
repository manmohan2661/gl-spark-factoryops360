package com.factoryops.inventory.mapper;

import com.factoryops.inventory.dto.request.WarehouseRequest;
import com.factoryops.inventory.dto.response.WarehouseResponse;
import com.factoryops.inventory.entity.Warehouse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class WarehouseMapperImpl implements WarehouseMapper {

    @Override
    public Warehouse toEntity(WarehouseRequest request) {
        if (request == null) {
            return null;
        }
        return Warehouse.builder()
                .code(request.getCode())
                .name(request.getName())
                .location(request.getLocation())
                .capacity(request.getCapacity())
                .active(request.getActive())
                .build();
    }

    @Override
    public WarehouseResponse toResponse(Warehouse entity) {
        if (entity == null) {
            return null;
        }
        return WarehouseResponse.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .location(entity.getLocation())
                .capacity(entity.getCapacity())
                .active(entity.getActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    @Override
    public List<WarehouseResponse> toResponseList(List<Warehouse> entities) {
        if (entities == null) {
            return List.of();
        }
        return entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
