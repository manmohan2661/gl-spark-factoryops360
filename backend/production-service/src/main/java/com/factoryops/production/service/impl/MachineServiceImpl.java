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
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MachineServiceImpl implements MachineService {

    private static final String RESOURCE_NAME = "Machine";

    private final MachineRepository machineRepository;
    private final MachineMapper machineMapper;

    @Transactional
    @Override
    public MachineResponse create(MachineRequest request) {

        log.info("Creating machine with code : {}", request.getMachineCode());

        machineRepository.findByMachineCode(request.getMachineCode())
                .ifPresent(machine -> {
                    throw new BusinessException(
                            "Machine already exists with code: "
                                    + request.getMachineCode());
                });

        validateInstallationDate(request.getInstallationDate());

        Machine machine = machineMapper.toEntity(request);

        LocalDateTime now = LocalDateTime.now();

        machine.setCreatedAt(now);
        machine.setUpdatedAt(now);

        Machine saved = machineRepository.save(machine);

        log.info("Machine created successfully with id : {}", saved.getId());

        return machineMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public MachineResponse getById(Long id) {

        log.debug("Fetching machine {}", id);

        Machine machine = findMachineOrThrow(id);

        return machineMapper.toResponse(machine);
    }

    @Transactional(readOnly = true)
    @Override
    public List<MachineResponse> getAll() {

        log.debug("Fetching all machines");

        return machineMapper.toResponseList(
                machineRepository.findAll()
        );
    }

    @Transactional
    @Override
    public MachineResponse update(Long id,
                                  MachineRequest request) {

        log.info("Updating machine {}", id);

        Machine machine = findMachineOrThrow(id);

        machineRepository.findByMachineCode(request.getMachineCode())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new BusinessException(
                            "Machine already exists with code: "
                                    + request.getMachineCode());
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

        log.info("Machine updated successfully : {}", updated.getId());

        return machineMapper.toResponse(updated);
    }

    @Transactional
    @Override
    public void delete(Long id) {

        log.info("Deleting machine {}", id);

        Machine machine = findMachineOrThrow(id);

        try {

            machineRepository.delete(machine);

            log.info("Machine deleted successfully : {}", id);

        } catch (DataIntegrityViolationException ex) {

            log.error("Failed to delete machine {}", id);

            throw new BusinessException(
                    "Cannot delete machine '"
                            + machine.getMachineCode()
                            + "' because it is referenced by existing production batches or maintenance records."
            );
        }
    }

    private Machine findMachineOrThrow(Long id) {

        return machineRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                RESOURCE_NAME,
                                id
                        ));
    }

    private void validateInstallationDate(LocalDate installationDate) {

        if (installationDate != null
                && installationDate.isAfter(LocalDate.now())) {

            throw new BusinessException(
                    "Installation date cannot be in the future."
            );
        }
    }
}