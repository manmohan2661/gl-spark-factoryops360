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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShiftServiceImpl implements ShiftService {

    private static final String RESOURCE_NAME = "Shift";

    private final ShiftRepository shiftRepository;
    private final ShiftMapper shiftMapper;

    @Transactional
    @Override
    public ShiftResponse create(ShiftRequest request) {

        log.info("Creating shift {}", request.getShiftName());

        shiftRepository.findByShiftName(request.getShiftName())
                .ifPresent(existing -> {
                    throw new BusinessException(
                            "Shift already exists with name: "
                                    + request.getShiftName());
                });

        validateShift(request);

        Shift shift = shiftMapper.toEntity(request);

        LocalDateTime now = LocalDateTime.now();

        shift.setCreatedAt(now);
        shift.setUpdatedAt(now);

        Shift saved = shiftRepository.save(shift);

        log.info("Shift created successfully : {}", saved.getId());

        return shiftMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public ShiftResponse getById(Long id) {

        log.debug("Fetching shift {}", id);

        Shift shift = findShiftOrThrow(id);

        return shiftMapper.toResponse(shift);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ShiftResponse> getAll() {

        log.debug("Fetching all shifts");

        return shiftMapper.toResponseList(
                shiftRepository.findAll()
        );
    }

    @Transactional
    @Override
    public ShiftResponse update(Long id,
                                ShiftRequest request) {

        log.info("Updating shift {}", id);

        Shift shift = findShiftOrThrow(id);

        shiftRepository.findByShiftName(request.getShiftName())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new BusinessException(
                            "Shift already exists with name: "
                                    + request.getShiftName());
                });

        validateShift(request);

        shift.setShiftName(request.getShiftName());
        shift.setStartTime(request.getStartTime());
        shift.setEndTime(request.getEndTime());
        shift.setSupervisorName(request.getSupervisorName());
        shift.setUpdatedAt(LocalDateTime.now());

        Shift updated = shiftRepository.save(shift);

        log.info("Shift updated successfully : {}", updated.getId());

        return shiftMapper.toResponse(updated);
    }

    @Transactional
    @Override
    public void delete(Long id) {

        log.info("Deleting shift {}", id);

        Shift shift = findShiftOrThrow(id);

        shiftRepository.delete(shift);

        log.info("Shift deleted successfully : {}", id);
    }

    private void validateShift(ShiftRequest request) {

        if (!request.getEndTime().isAfter(request.getStartTime())) {

            throw new BusinessException(
                    "End time must be after start time"
            );
        }
    }

    private Shift findShiftOrThrow(Long id) {

        return shiftRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                RESOURCE_NAME,
                                id
                        ));
    }
}