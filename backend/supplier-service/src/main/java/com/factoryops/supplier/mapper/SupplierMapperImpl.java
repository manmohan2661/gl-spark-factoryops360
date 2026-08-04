package com.factoryops.supplier.mapper;

import com.factoryops.supplier.dto.request.SupplierRequest;
import com.factoryops.supplier.dto.response.SupplierResponse;
import com.factoryops.supplier.entity.Supplier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SupplierMapperImpl implements SupplierMapper {

    @Override
    public Supplier toEntity(SupplierRequest request) {
        if (request == null) {
            return null;
        }
        return Supplier.builder()
                .code(request.getCode())
                .name(request.getName())
                .contactPerson(request.getContactPerson())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .city(request.getCity())
                .country(request.getCountry())
                .status(request.getStatus())
                .rating(request.getRating())
                .build();
    }

    @Override
    public SupplierResponse toResponse(Supplier entity) {
        if (entity == null) {
            return null;
        }
        return SupplierResponse.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .contactPerson(entity.getContactPerson())
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .address(entity.getAddress())
                .city(entity.getCity())
                .country(entity.getCountry())
                .status(entity.getStatus())
                .rating(entity.getRating())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    @Override
    public List<SupplierResponse> toResponseList(List<Supplier> entities) {
        if (entities == null) {
            return List.of();
        }
        return entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
