package com.factoryops.production.mapper;

import com.factoryops.production.dto.request.MachineMaintenanceRequest;
import com.factoryops.production.dto.response.MachineMaintenanceResponse;
import com.factoryops.production.entity.MachineMaintenance;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MachineMaintenanceMapperImpl implements MachineMaintenanceMapper {

    @Override
    public MachineMaintenance toEntity(MachineMaintenanceRequest request) {

        if (request == null) {
            return null;
        }

        return MachineMaintenance.builder()
                .maintenanceType(request.getMaintenanceType())
                .status(request.getStatus())
                .scheduledDate(request.getScheduledDate())
                .completedDate(request.getCompletedDate())
                .remarks(request.getRemarks())
                .build();
    }

    @Override
    public MachineMaintenanceResponse toResponse(MachineMaintenance entity) {

        if (entity == null) {
            return null;
        }

        return MachineMaintenanceResponse.builder()
                .id(entity.getId())
                .maintenanceType(entity.getMaintenanceType())
                .status(entity.getStatus())
                .scheduledDate(entity.getScheduledDate())
                .completedDate(entity.getCompletedDate())
                .remarks(entity.getRemarks())
                .machineId(
                        entity.getMachine() != null
                                ? entity.getMachine().getId()
                                : null
                )
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    @Override
    public List<MachineMaintenanceResponse> toResponseList(List<MachineMaintenance> entities) {

        if (entities == null) {
            return List.of();
        }

        return entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}