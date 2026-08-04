package com.factoryops.inventory.mapper;

import com.factoryops.inventory.dto.request.MaterialRequest;
import com.factoryops.inventory.dto.response.MaterialResponse;
import com.factoryops.inventory.entity.Material;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MaterialMapperImpl implements MaterialMapper {

    @Override
    public Material toEntity(MaterialRequest request) {
        if (request == null) {
            return null;
        }
        return Material.builder()
                .code(request.getCode())
                .name(request.getName())
                .description(request.getDescription())
                .unitOfMeasure(request.getUnitOfMeasure())
                .category(request.getCategory())
                .reorderLevel(request.getReorderLevel())
                .active(request.getActive())
                .build();
    }

    @Override
    public MaterialResponse toResponse(Material entity) {
        if (entity == null) {
            return null;
        }
        return MaterialResponse.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .description(entity.getDescription())
                .unitOfMeasure(entity.getUnitOfMeasure())
                .category(entity.getCategory())
                .reorderLevel(entity.getReorderLevel())
                .active(entity.getActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    @Override
    public List<MaterialResponse> toResponseList(List<Material> entities) {
        if (entities == null) {
            return List.of();
        }
        return entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
