package com.factoryops.inventory.service.impl;

import com.factoryops.inventory.dto.request.WarehouseRequest;
import com.factoryops.inventory.dto.response.WarehouseResponse;
import com.factoryops.inventory.entity.Warehouse;
import com.factoryops.inventory.exception.BusinessException;
import com.factoryops.inventory.exception.ResourceNotFoundException;
import com.factoryops.inventory.mapper.WarehouseMapper;
import com.factoryops.inventory.repository.WarehouseRepository;
import com.factoryops.inventory.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

    private static final String RESOURCE_NAME = "Warehouse";

    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;

    @Transactional
    @Override
    public WarehouseResponse create(WarehouseRequest request) {

        log.info("Creating warehouse {}", request.getCode());

        if (warehouseRepository.existsByCode(request.getCode())) {
            throw new BusinessException("Warehouse code already exists.");
        }

        Warehouse warehouse = warehouseMapper.toEntity(request);

        Warehouse savedWarehouse = warehouseRepository.save(warehouse);

        log.info("Warehouse created successfully : {}", savedWarehouse.getId());

        return warehouseMapper.toResponse(savedWarehouse);
    }

    @Transactional(readOnly = true)
    @Override
    public WarehouseResponse getById(Long id) {

        log.debug("Fetching warehouse {}", id);

        Warehouse warehouse = findWarehouseOrThrow(id);

        return warehouseMapper.toResponse(warehouse);
    }

    @Transactional(readOnly = true)
    @Override
    public List<WarehouseResponse> getAll() {

        log.debug("Fetching all warehouses");

        return warehouseMapper.toResponseList(
                warehouseRepository.findAll()
        );
    }

    @Transactional
    @Override
    public WarehouseResponse update(Long id,
                                    WarehouseRequest request) {

        log.info("Updating warehouse {}", id);

        Warehouse warehouse = findWarehouseOrThrow(id);

        warehouseRepository.findByCode(request.getCode())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new BusinessException(
                            "Warehouse code already exists.");
                });

        warehouse.setCode(request.getCode());
        warehouse.setName(request.getName());
        warehouse.setLocation(request.getLocation());
        warehouse.setCapacity(request.getCapacity());
        warehouse.setActive(request.getActive());
        Warehouse updatedWarehouse = warehouseRepository.save(warehouse);

        log.info("Warehouse updated successfully : {}", updatedWarehouse.getId());

        return warehouseMapper.toResponse(updatedWarehouse);
    }

    @Transactional
    @Override
    public void delete(Long id) {

        log.info("Deleting warehouse {}", id);

        Warehouse warehouse = findWarehouseOrThrow(id);

        try {

            warehouseRepository.delete(warehouse);

            log.info("Warehouse deleted successfully : {}", id);

        } catch (DataIntegrityViolationException ex) {

            log.error("Failed to delete warehouse {}", id);

            throw new BusinessException(
                    "Cannot delete warehouse '"
                            + warehouse.getCode()
                            + "' because it is referenced by existing inventory records."
            );
        }
    }

    private Warehouse findWarehouseOrThrow(Long id) {

        return warehouseRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                RESOURCE_NAME,
                                id
                        ));
    }
}