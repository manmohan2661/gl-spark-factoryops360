package com.factoryops.inventory.service.impl;

import com.factoryops.inventory.dto.request.InventoryRequest;
import com.factoryops.inventory.dto.response.InventoryResponse;
import com.factoryops.inventory.entity.Inventory;
import com.factoryops.inventory.entity.Material;
import com.factoryops.inventory.entity.Warehouse;
import com.factoryops.inventory.exception.BusinessException;
import com.factoryops.inventory.exception.ResourceNotFoundException;
import com.factoryops.inventory.mapper.InventoryMapper;
import com.factoryops.inventory.repository.InventoryRepository;
import com.factoryops.inventory.repository.MaterialRepository;
import com.factoryops.inventory.repository.WarehouseRepository;
import com.factoryops.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private static final String RESOURCE_NAME = "Inventory";

    private final InventoryRepository inventoryRepository;
    private final InventoryMapper inventoryMapper;
    private final MaterialRepository materialRepository;
    private final WarehouseRepository warehouseRepository;

    @Transactional
    @Override
    public InventoryResponse create(InventoryRequest request) {

        log.info("Creating inventory for material {} and warehouse {}",
                request.getMaterialId(),
                request.getWarehouseId());

        Material material = findMaterialOrThrow(request.getMaterialId());
        Warehouse warehouse = findWarehouseOrThrow(request.getWarehouseId());

        if (inventoryRepository.existsByMaterialIdAndWarehouseId(
                material.getId(),
                warehouse.getId())) {

            throw new BusinessException(
                    "Inventory already exists for this Material and Warehouse.");
        }

        Inventory inventory = inventoryMapper.toEntity(request);

        inventory.setMaterial(material);
        inventory.setWarehouse(warehouse);
        inventory.setLastUpdated(LocalDateTime.now());

        Inventory savedInventory = inventoryRepository.save(inventory);

        log.info("Inventory created successfully : {}", savedInventory.getId());

        return inventoryMapper.toResponse(savedInventory);
    }

    @Transactional(readOnly = true)
    @Override
    public InventoryResponse getById(Long id) {

        log.debug("Fetching inventory {}", id);

        return inventoryMapper.toResponse(findInventoryOrThrow(id));
    }

    @Transactional(readOnly = true)
    @Override
    public List<InventoryResponse> getAll() {

        log.debug("Fetching all inventory records");

        return inventoryMapper.toResponseList(
                inventoryRepository.findAll());
    }

    @Transactional
    @Override
    public InventoryResponse update(Long id,
                                    InventoryRequest request) {

        log.info("Updating inventory {}", id);

        Inventory inventory = findInventoryOrThrow(id);

        Material material = findMaterialOrThrow(request.getMaterialId());
        Warehouse warehouse = findWarehouseOrThrow(request.getWarehouseId());

        inventory.setMaterial(material);
        inventory.setWarehouse(warehouse);
        inventory.setQuantityAvailable(request.getQuantityAvailable());
        inventory.setQuantityReserved(request.getQuantityReserved());
        inventory.setLastUpdated(LocalDateTime.now());

        Inventory updatedInventory = inventoryRepository.save(inventory);

        log.info("Inventory updated successfully : {}", updatedInventory.getId());

        return inventoryMapper.toResponse(updatedInventory);
    }

    @Transactional
    @Override
    public void delete(Long id) {

        log.info("Deleting inventory {}", id);

        inventoryRepository.delete(findInventoryOrThrow(id));

        log.info("Inventory deleted successfully : {}", id);
    }

    private Inventory findInventoryOrThrow(Long id) {

        return inventoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                RESOURCE_NAME,
                                id
                        ));
    }

    private Material findMaterialOrThrow(Long id) {

        return materialRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Material",
                                id
                        ));
    }

    private Warehouse findWarehouseOrThrow(Long id) {

        return warehouseRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Warehouse",
                                id
                        ));
    }
}