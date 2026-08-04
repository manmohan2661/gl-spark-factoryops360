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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

    private static final String RESOURCE_NAME = "Warehouse";

    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;

    @Override
    public WarehouseResponse create(WarehouseRequest request) {

        if (warehouseRepository.existsByCode(request.getCode())) {
            throw new BusinessException("Warehouse code already exists.");
        }

        Warehouse warehouse = warehouseMapper.toEntity(request);

        Warehouse savedWarehouse = warehouseRepository.save(warehouse);

        return warehouseMapper.toResponse(savedWarehouse);
    }

    @Override
    public WarehouseResponse getById(Long id) {

        Warehouse warehouse = findWarehouseOrThrow(id);

        return warehouseMapper.toResponse(warehouse);
    }

    @Override
    public List<WarehouseResponse> getAll() {

        return warehouseMapper.toResponseList(
                warehouseRepository.findAll()
        );
    }

    @Override
    public WarehouseResponse update(Long id, WarehouseRequest request) {

        Warehouse warehouse = findWarehouseOrThrow(id);

        warehouseRepository.findByCode(request.getCode())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new BusinessException("Warehouse code already exists.");
                });

        warehouse.setCode(request.getCode());
        warehouse.setName(request.getName());
        warehouse.setLocation(request.getLocation());
        warehouse.setCapacity(request.getCapacity());
        warehouse.setActive(request.getActive());

        Warehouse updatedWarehouse = warehouseRepository.save(warehouse);

        return warehouseMapper.toResponse(updatedWarehouse);
    }

    @Override
    public void delete(Long id) {

        Warehouse warehouse = findWarehouseOrThrow(id);

        try {

            warehouseRepository.delete(warehouse);

        } catch (DataIntegrityViolationException ex) {

            throw new BusinessException(
                    "Cannot delete warehouse '" + warehouse.getCode()
                            + "' because it is referenced by existing inventory records."
            );
        }
    }

    private Warehouse findWarehouseOrThrow(Long id) {

        return warehouseRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(RESOURCE_NAME, id));
    }
}