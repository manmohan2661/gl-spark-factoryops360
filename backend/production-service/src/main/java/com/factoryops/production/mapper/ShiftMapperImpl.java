package com.factoryops.production.mapper;

import com.factoryops.production.dto.request.ShiftRequest;
import com.factoryops.production.dto.response.ShiftResponse;
import com.factoryops.production.entity.Shift;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ShiftMapperImpl implements ShiftMapper {

    @Override
    public Shift toEntity(ShiftRequest request) {

        if (request == null) {
            return null;
        }

        return Shift.builder()
                .shiftName(request.getShiftName())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .supervisorName(request.getSupervisorName())
                .build();
    }

    @Override
    public ShiftResponse toResponse(Shift entity) {

        if (entity == null) {
            return null;
        }

        return ShiftResponse.builder()
                .id(entity.getId())
                .shiftName(entity.getShiftName())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .supervisorName(entity.getSupervisorName())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    @Override
    public List<ShiftResponse> toResponseList(List<Shift> entities) {

        if (entities == null) {
            return List.of();
        }

        return entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}