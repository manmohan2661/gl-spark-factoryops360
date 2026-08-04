package com.factoryops.production.service.impl;

import com.factoryops.production.dto.request.ProductionBatchRequest;
import com.factoryops.production.dto.response.ProductionBatchResponse;
import com.factoryops.production.entity.BatchStatus;
import com.factoryops.production.entity.Machine;
import com.factoryops.production.entity.MachineStatus;
import com.factoryops.production.entity.ProductionBatch;
import com.factoryops.production.entity.ProductionOrder;
import com.factoryops.production.entity.ProductionOrderStatus;
import com.factoryops.production.entity.Shift;
import com.factoryops.production.exception.BusinessException;
import com.factoryops.production.exception.ResourceNotFoundException;
import com.factoryops.production.mapper.ProductionBatchMapper;
import com.factoryops.production.repository.MachineRepository;
import com.factoryops.production.repository.ProductionBatchRepository;
import com.factoryops.production.repository.ProductionOrderRepository;
import com.factoryops.production.repository.ShiftRepository;
import com.factoryops.production.service.ProductionBatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductionBatchServiceImpl implements ProductionBatchService {

    private static final String RESOURCE_NAME = "ProductionBatch";

    private final ProductionBatchRepository productionBatchRepository;
    private final ProductionBatchMapper productionBatchMapper;
    private final ProductionOrderRepository productionOrderRepository;
    private final MachineRepository machineRepository;
    private final ShiftRepository shiftRepository;

    @Override
    public ProductionBatchResponse create(ProductionBatchRequest request) {

        productionBatchRepository.findByBatchNumber(request.getBatchNumber())
                .ifPresent(batch -> {
                    throw new BusinessException(
                            "Batch already exists with number: " + request.getBatchNumber());
                });

        ProductionOrder productionOrder = findProductionOrder(request.getProductionOrderId());

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
                        "Machine " + machine.getMachineCode() + " is not operational.");
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

        ProductionBatch batch = productionBatchMapper.toEntity(request);

        batch.setProductionOrder(productionOrder);
        batch.setMachine(machine);
        batch.setShift(shift);

        LocalDateTime now = LocalDateTime.now();

        batch.setCreatedAt(now);
        batch.setUpdatedAt(now);

        if (batch.getStatus() == null) {
            batch.setStatus(BatchStatus.PLANNED);
        }

        ProductionBatch saved = productionBatchRepository.save(batch);

        return productionBatchMapper.toResponse(saved);
    }

    @Override
    public ProductionBatchResponse getById(Long id) {

        return productionBatchMapper.toResponse(findBatch(id));
    }

    @Override
    public List<ProductionBatchResponse> getAll() {

        return productionBatchMapper.toResponseList(
                productionBatchRepository.findAll());
    }

    @Override
    public ProductionBatchResponse update(Long id,
                                          ProductionBatchRequest request) {

        ProductionBatch batch = findBatch(id);

        productionBatchRepository.findByBatchNumber(request.getBatchNumber())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new BusinessException(
                            "Batch already exists with number: " + request.getBatchNumber());
                });

        ProductionOrder productionOrder = findProductionOrder(request.getProductionOrderId());

        Machine machine = null;

        if (request.getMachineId() != null) {

            machine = findMachine(request.getMachineId());

            if (machine.getStatus() != MachineStatus.OPERATIONAL) {
                throw new BusinessException(
                        "Machine " + machine.getMachineCode() + " is not operational.");
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

        ProductionBatch updated = productionBatchRepository.save(batch);

        return productionBatchMapper.toResponse(updated);
    }

    @Override
    public void delete(Long id) {

        ProductionBatch batch = findBatch(id);

        try {
            productionBatchRepository.delete(batch);
        } catch (DataIntegrityViolationException ex) {

            throw new BusinessException(
                    "Cannot delete production batch because it is referenced by other records.");
        }
    }

    private ProductionBatch findBatch(Long id) {

        return productionBatchRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(RESOURCE_NAME, id));
    }

    private ProductionOrder findProductionOrder(Long id) {

        return productionOrderRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("ProductionOrder", id));
    }

    private Machine findMachine(Long id) {

        return machineRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Machine", id));
    }

    private Shift findShift(Long id) {

        return shiftRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Shift", id));
    }
}