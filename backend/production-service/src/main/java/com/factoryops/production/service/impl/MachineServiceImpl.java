package com.factoryops.production.service.impl;

import com.factoryops.production.dto.request.MachineRequest;
import com.factoryops.production.dto.response.MachineResponse;
import com.factoryops.production.entity.Machine;
import com.factoryops.production.exception.BusinessException;
import com.factoryops.production.exception.ResourceNotFoundException;
import com.factoryops.production.mapper.MachineMapper;
import com.factoryops.production.repository.MachineRepository;
import com.factoryops.production.service.MachineService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MachineServiceImpl implements MachineService {

    private static final String RESOURCE_NAME = "Machine";

    private final MachineRepository machineRepository;
    private final MachineMapper machineMapper;

    @Override
    public MachineResponse create(MachineRequest request) {

        machineRepository.findByMachineCode(request.getMachineCode())
                .ifPresent(machine -> {
                    throw new BusinessException(
                            "Machine already exists with code: " + request.getMachineCode());
                });

        validateInstallationDate(request.getInstallationDate());

        Machine machine = machineMapper.toEntity(request);

        LocalDateTime now = LocalDateTime.now();
        machine.setCreatedAt(now);
        machine.setUpdatedAt(now);

        Machine saved = machineRepository.save(machine);

        return machineMapper.toResponse(saved);
    }

    @Override
    public MachineResponse getById(Long id) {

        Machine machine = findMachineOrThrow(id);

        return machineMapper.toResponse(machine);
    }

    @Override
    public List<MachineResponse> getAll() {

        return machineMapper.toResponseList(machineRepository.findAll());
    }

    @Override
    public MachineResponse update(Long id, MachineRequest request) {

        Machine machine = findMachineOrThrow(id);

        machineRepository.findByMachineCode(request.getMachineCode())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new BusinessException(
                            "Machine already exists with code: " + request.getMachineCode());
                });

        validateInstallationDate(request.getInstallationDate());

        machine.setMachineCode(request.getMachineCode());
        machine.setName(request.getName());
        machine.setType(request.getType());
        machine.setStatus(request.getStatus());
        machine.setLocation(request.getLocation());
        machine.setInstallationDate(request.getInstallationDate());
        machine.setUpdatedAt(LocalDateTime.now());

        Machine updated = machineRepository.save(machine);

        return machineMapper.toResponse(updated);
    }

    @Override
    public void delete(Long id) {

        Machine machine = findMachineOrThrow(id);

        try {
            machineRepository.delete(machine);
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessException(
                    "Cannot delete machine '" + machine.getMachineCode()
                            + "' because it is referenced by existing production batches or maintenance records.");
        }
    }

    private Machine findMachineOrThrow(Long id) {

        return machineRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(RESOURCE_NAME, id));
    }

    private void validateInstallationDate(LocalDate installationDate) {

        if (installationDate != null && installationDate.isAfter(LocalDate.now())) {
            throw new BusinessException(
                    "Installation date cannot be in the future.");
        }
    }
}