package com.factoryops.production.service.impl;

import com.factoryops.production.dto.request.ShiftRequest;
import com.factoryops.production.dto.response.ShiftResponse;
import com.factoryops.production.entity.Shift;
import com.factoryops.production.exception.BusinessException;
import com.factoryops.production.exception.ResourceNotFoundException;
import com.factoryops.production.mapper.ShiftMapper;
import com.factoryops.production.repository.ShiftRepository;
import com.factoryops.production.service.ShiftService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShiftServiceImpl implements ShiftService {

    private static final String RESOURCE_NAME = "Shift";

    private final ShiftRepository shiftRepository;
    private final ShiftMapper shiftMapper;

    @Override
    public ShiftResponse create(ShiftRequest request) {

        shiftRepository.findByShiftName(request.getShiftName())
                .ifPresent(existing -> {
                    throw new BusinessException(
                            "Shift already exists with name: " + request.getShiftName());
                });

        validateShift(request);

        Shift shift = shiftMapper.toEntity(request);

        LocalDateTime now = LocalDateTime.now();
        shift.setCreatedAt(now);
        shift.setUpdatedAt(now);

        Shift saved = shiftRepository.save(shift);

        return shiftMapper.toResponse(saved);
    }

    @Override
    public ShiftResponse getById(Long id) {

        Shift shift = findShiftOrThrow(id);

        return shiftMapper.toResponse(shift);
    }

    @Override
    public List<ShiftResponse> getAll() {

        return shiftMapper.toResponseList(shiftRepository.findAll());
    }

    @Override
    public ShiftResponse update(Long id, ShiftRequest request) {

        Shift shift = findShiftOrThrow(id);

        shiftRepository.findByShiftName(request.getShiftName())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new BusinessException(
                            "Shift already exists with name: " + request.getShiftName());
                });

        validateShift(request);

        shift.setShiftName(request.getShiftName());
        shift.setStartTime(request.getStartTime());
        shift.setEndTime(request.getEndTime());
        shift.setSupervisorName(request.getSupervisorName());
        shift.setUpdatedAt(LocalDateTime.now());

        Shift updated = shiftRepository.save(shift);

        return shiftMapper.toResponse(updated);
    }

    @Override
    public void delete(Long id) {

        Shift shift = findShiftOrThrow(id);

        shiftRepository.delete(shift);
    }

    private void validateShift(ShiftRequest request) {

        if (!request.getEndTime().isAfter(request.getStartTime())) {
            throw new BusinessException(
                    "End time must be after start time");
        }
    }

    private Shift findShiftOrThrow(Long id) {

        return shiftRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(RESOURCE_NAME, id));
    }
}