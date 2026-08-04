package com.factoryops.production.service.impl;

import com.factoryops.production.dto.request.ProductionBatchRequest;
import com.factoryops.production.dto.response.ProductionBatchResponse;
import com.factoryops.production.entity.*;
import com.factoryops.production.exception.BusinessException;
import com.factoryops.production.exception.ResourceNotFoundException;
import com.factoryops.production.mapper.ProductionBatchMapper;
import com.factoryops.production.repository.MachineRepository;
import com.factoryops.production.repository.ProductionBatchRepository;
import com.factoryops.production.repository.ProductionOrderRepository;
import com.factoryops.production.repository.ShiftRepository;
import com.factoryops.production.service.ProductionBatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductionBatchServiceImpl implements ProductionBatchService {

    private static final String RESOURCE_NAME = "ProductionBatch";

    private final ProductionBatchRepository productionBatchRepository;
    private final ProductionBatchMapper productionBatchMapper;
    private final ProductionOrderRepository productionOrderRepository;
    private final MachineRepository machineRepository;
    private final ShiftRepository shiftRepository;

    @Transactional
    @Override
    public ProductionBatchResponse create(ProductionBatchRequest request) {

        log.info("Creating production batch {}", request.getBatchNumber());

        productionBatchRepository.findByBatchNumber(request.getBatchNumber())
                .ifPresent(batch -> {
                    throw new BusinessException(
                            "Batch already exists with number: "
                                    + request.getBatchNumber());
                });

        ProductionOrder productionOrder =
                findProductionOrder(request.getProductionOrderId());

        if (productionOrder.getStatus() == ProductionOrderStatus.COMPLETED
                || productionOrder.getStatus() == ProductionOrderStatus.CANCELLED) {

            throw new BusinessException(
                    "Cannot create batch for completed/cancelled production order.");
        }

        Machine machine = null;

        if (request.getMachineId() != null) {

            machine = findMachine(request.getMachineId());

            if (machine.getStatus() != MachineStatus.OPERATIONAL) {

                throw new BusinessException(
                        "Machine "
                                + machine.getMachineCode()
                                + " is not operational.");
            }
        }

        Shift shift = null;

        if (request.getShiftId() != null) {

            shift = findShift(request.getShiftId());
        }

        if (request.getStartTime() != null
                && request.getEndTime() != null
                && request.getEndTime().isBefore(request.getStartTime())) {

            throw new BusinessException(
                    "End time cannot be before start time.");
        }

        ProductionBatch batch =
                productionBatchMapper.toEntity(request);

        batch.setProductionOrder(productionOrder);
        batch.setMachine(machine);
        batch.setShift(shift);

        LocalDateTime now = LocalDateTime.now();

        batch.setCreatedAt(now);
        batch.setUpdatedAt(now);

        if (batch.getStatus() == null) {
            batch.setStatus(BatchStatus.PLANNED);
        }

        ProductionBatch saved =
                productionBatchRepository.save(batch);

        log.info("Production batch created successfully : {}", saved.getId());

        return productionBatchMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public ProductionBatchResponse getById(Long id) {

        log.debug("Fetching production batch {}", id);

        return productionBatchMapper.toResponse(findBatch(id));
    }

    @Transactional(readOnly = true)
    @Override
    public List<ProductionBatchResponse> getAll() {

        log.debug("Fetching all production batches");

        return productionBatchMapper.toResponseList(
                productionBatchRepository.findAll());
    }

    @Transactional
    @Override
    public ProductionBatchResponse update(Long id,
                                          ProductionBatchRequest request) {

        log.info("Updating production batch {}", id);

        ProductionBatch batch = findBatch(id);

        productionBatchRepository.findByBatchNumber(request.getBatchNumber())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new BusinessException(
                            "Batch already exists with number: "
                                    + request.getBatchNumber());
                });

        ProductionOrder productionOrder =
                findProductionOrder(request.getProductionOrderId());

        Machine machine = null;

        if (request.getMachineId() != null) {

            machine = findMachine(request.getMachineId());

            if (machine.getStatus() != MachineStatus.OPERATIONAL) {

                throw new BusinessException(
                        "Machine "
                                + machine.getMachineCode()
                                + " is not operational.");
            }
        }

        Shift shift = null;

        if (request.getShiftId() != null) {

            shift = findShift(request.getShiftId());
        }

        if (request.getStartTime() != null
                && request.getEndTime() != null
                && request.getEndTime().isBefore(request.getStartTime())) {

            throw new BusinessException(
                    "End time cannot be before start time.");
        }

        batch.setBatchNumber(request.getBatchNumber());
        batch.setQuantity(request.getQuantity());
        batch.setStatus(request.getStatus());
        batch.setStartTime(request.getStartTime());
        batch.setEndTime(request.getEndTime());
        batch.setProductionOrder(productionOrder);
        batch.setMachine(machine);
        batch.setShift(shift);
        batch.setUpdatedAt(LocalDateTime.now());

        ProductionBatch updated =
                productionBatchRepository.save(batch);

        log.info("Production batch updated successfully : {}", updated.getId());

        return productionBatchMapper.toResponse(updated);
    }

    @Transactional
    @Override
    public void delete(Long id) {

        log.info("Deleting production batch {}", id);

        ProductionBatch batch = findBatch(id);

        try {

            productionBatchRepository.delete(batch);

            log.info("Production batch deleted successfully : {}", id);

        } catch (DataIntegrityViolationException ex) {

            log.error("Failed to delete production batch {}", id);

            throw new BusinessException(
                    "Cannot delete production batch because it is referenced by other records."
            );
        }
    }

    private ProductionBatch findBatch(Long id) {

        return productionBatchRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                RESOURCE_NAME,
                                id
                        ));
    }

    private ProductionOrder findProductionOrder(Long id) {

        return productionOrderRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "ProductionOrder",
                                id
                        ));
    }

    private Machine findMachine(Long id) {

        return machineRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Machine",
                                id
                        ));
    }

    private Shift findShift(Long id) {

        return shiftRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Shift",
                                id
                        ));
    }
}