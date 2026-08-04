package com.factoryops.auth.mapper;

import com.factoryops.auth.dto.request.RoleRequest;
import com.factoryops.auth.dto.response.RoleResponse;
import com.factoryops.auth.entity.Role;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RoleMapper {

    public Role toEntity(RoleRequest request) {

        if (request == null) {
            return null;
        }

        return Role.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
    }

    public RoleResponse toResponse(Role entity) {

        if (entity == null) {
            return null;
        }

        return RoleResponse.builder()
                .roleId(entity.getRoleId())
                .name(entity.getName())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public List<RoleResponse> toResponseList(List<Role> entities) {

        return entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}