package com.factoryops.production.service.impl;

import com.factoryops.production.dto.request.MachineMaintenanceRequest;
import com.factoryops.production.dto.response.MachineMaintenanceResponse;
import com.factoryops.production.entity.Machine;
import com.factoryops.production.entity.MachineMaintenance;
import com.factoryops.production.entity.MaintenanceStatus;
import com.factoryops.production.exception.BusinessException;
import com.factoryops.production.exception.ResourceNotFoundException;
import com.factoryops.production.mapper.MachineMaintenanceMapper;
import com.factoryops.production.repository.MachineMaintenanceRepository;
import com.factoryops.production.repository.MachineRepository;
import com.factoryops.production.service.MachineMaintenanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MachineMaintenanceServiceImpl implements MachineMaintenanceService {

    private static final String RESOURCE_NAME = "Machine Maintenance";

    private final MachineMaintenanceRepository machineMaintenanceRepository;
    private final MachineMaintenanceMapper machineMaintenanceMapper;
    private final MachineRepository machineRepository;

    @Transactional
    @Override
    public MachineMaintenanceResponse create(MachineMaintenanceRequest request) {

        log.info("Creating maintenance record for machine {}", request.getMachineId());

        Machine machine = findMachineOrThrow(request.getMachineId());

        validateMaintenance(request);

        MachineMaintenance maintenance =
                machineMaintenanceMapper.toEntity(request);

        maintenance.setMachine(machine);

        LocalDateTime now = LocalDateTime.now();

        maintenance.setCreatedAt(now);
        maintenance.setUpdatedAt(now);

        MachineMaintenance saved =
                machineMaintenanceRepository.save(maintenance);

        log.info("Maintenance record created successfully : {}", saved.getId());

        return machineMaintenanceMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public MachineMaintenanceResponse getById(Long id) {

        log.debug("Fetching maintenance record {}", id);

        MachineMaintenance maintenance = findMaintenanceOrThrow(id);

        return machineMaintenanceMapper.toResponse(maintenance);
    }

    @Transactional(readOnly = true)
    @Override
    public List<MachineMaintenanceResponse> getAll() {

        log.debug("Fetching all maintenance records");

        return machineMaintenanceMapper.toResponseList(
                machineMaintenanceRepository.findAll()
        );
    }

    @Transactional
    @Override
    public MachineMaintenanceResponse update(Long id,
                                             MachineMaintenanceRequest request) {

        log.info("Updating maintenance record {}", id);

        MachineMaintenance maintenance = findMaintenanceOrThrow(id);

        Machine machine = findMachineOrThrow(request.getMachineId());

        validateMaintenance(request);

        maintenance.setMaintenanceType(request.getMaintenanceType());
        maintenance.setStatus(request.getStatus());
        maintenance.setScheduledDate(request.getScheduledDate());
        maintenance.setCompletedDate(request.getCompletedDate());
        maintenance.setRemarks(request.getRemarks());
        maintenance.setMachine(machine);
        maintenance.setUpdatedAt(LocalDateTime.now());

        MachineMaintenance updated =
                machineMaintenanceRepository.save(maintenance);

        log.info("Maintenance record updated successfully : {}", updated.getId());

        return machineMaintenanceMapper.toResponse(updated);
    }

    @Transactional
    @Override
    public void delete(Long id) {

        log.info("Deleting maintenance record {}", id);

        MachineMaintenance maintenance = findMaintenanceOrThrow(id);

        machineMaintenanceRepository.delete(maintenance);

        log.info("Maintenance record deleted successfully : {}", id);
    }

    /**
     * Business Validations
     */
    private void validateMaintenance(MachineMaintenanceRequest request) {

        if (request.getCompletedDate() != null
                && request.getScheduledDate() != null
                && request.getCompletedDate().isBefore(request.getScheduledDate())) {

            throw new BusinessException(
                    "Completed date cannot be before scheduled date");
        }

        if (request.getStatus() == MaintenanceStatus.COMPLETED
                && request.getCompletedDate() == null) {

            throw new BusinessException(
                    "Completed date is required when maintenance status is COMPLETED");
        }

        if (request.getStatus() != MaintenanceStatus.COMPLETED
                && request.getCompletedDate() != null) {

            throw new BusinessException(
                    "Completed date should only be provided when status is COMPLETED");
        }
    }

    private MachineMaintenance findMaintenanceOrThrow(Long id) {

        return machineMaintenanceRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                RESOURCE_NAME,
                                id
                        ));
    }

    private Machine findMachineOrThrow(Long id) {

        return machineRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Machine",
                                id
                        ));
    }
}