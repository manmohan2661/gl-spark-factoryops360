package com.factoryops.production.mapper;

import com.factoryops.production.dto.request.MachineRequest;
import com.factoryops.production.dto.response.MachineResponse;
import com.factoryops.production.entity.Machine;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MachineMapperImpl implements MachineMapper {

    @Override
    public Machine toEntity(MachineRequest request) {
        if (request == null) {
            return null;
        }

        return Machine.builder()
                .machineCode(request.getMachineCode())
                .name(request.getName())
                .type(request.getType())
                .status(request.getStatus())
                .location(request.getLocation())
                .installationDate(request.getInstallationDate())
                .build();
    }

    @Override
    public MachineResponse toResponse(Machine entity) {
        if (entity == null) {
            return null;
        }

        return MachineResponse.builder()
                .id(entity.getId())
                .machineCode(entity.getMachineCode())
                .name(entity.getName())
                .type(entity.getType())
                .status(entity.getStatus())
                .location(entity.getLocation())
                .installationDate(entity.getInstallationDate())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    @Override
    public List<MachineResponse> toResponseList(List<Machine> entities) {
        if (entities == null) {
            return List.of();
        }

        return entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}