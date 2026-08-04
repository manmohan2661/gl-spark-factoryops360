package com.factoryops.inventory.service.impl;

import com.factoryops.inventory.dto.request.InventoryTransactionRequest;
import com.factoryops.inventory.dto.response.InventoryTransactionResponse;
import com.factoryops.inventory.entity.Inventory;
import com.factoryops.inventory.entity.InventoryTransaction;
import com.factoryops.inventory.entity.TransactionType;
import com.factoryops.inventory.exception.BusinessException;
import com.factoryops.inventory.exception.ResourceNotFoundException;
import com.factoryops.inventory.mapper.InventoryTransactionMapper;
import com.factoryops.inventory.repository.InventoryRepository;
import com.factoryops.inventory.repository.InventoryTransactionRepository;
import com.factoryops.inventory.service.InventoryTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryTransactionServiceImpl implements InventoryTransactionService {

    private static final String RESOURCE_NAME = "InventoryTransaction";

    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final InventoryTransactionMapper inventoryTransactionMapper;
    private final InventoryRepository inventoryRepository;

    @Transactional
    @Override
    public InventoryTransactionResponse create(InventoryTransactionRequest request) {

        log.info("Creating inventory transaction for inventory {}",
                request.getInventoryId());

        Inventory inventory = findInventoryOrThrow(request.getInventoryId());

        applyStockMovement(
                inventory,
                request.getTransactionType(),
                request.getQuantity()
        );

        inventory.setLastUpdated(LocalDateTime.now());

        inventoryRepository.save(inventory);

        InventoryTransaction transaction =
                inventoryTransactionMapper.toEntity(request);

        transaction.setInventory(inventory);
        transaction.setTransactionDate(LocalDateTime.now());

        InventoryTransaction saved =
                inventoryTransactionRepository.save(transaction);

        log.info("Inventory transaction created successfully : {}",
                saved.getId());

        return inventoryTransactionMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public InventoryTransactionResponse getById(Long id) {

        log.debug("Fetching inventory transaction {}", id);

        return inventoryTransactionMapper.toResponse(
                findTransactionOrThrow(id)
        );
    }

    @Transactional(readOnly = true)
    @Override
    public List<InventoryTransactionResponse> getAll() {

        log.debug("Fetching all inventory transactions");

        return inventoryTransactionMapper.toResponseList(
                inventoryTransactionRepository.findAll()
        );
    }

    @Transactional
    @Override
    public InventoryTransactionResponse update(Long id,
                                               InventoryTransactionRequest request) {

        log.info("Updating inventory transaction {}", id);

        InventoryTransaction transaction =
                findTransactionOrThrow(id);

        if (request.getTransactionType() != transaction.getTransactionType()
                || !request.getQuantity().equals(transaction.getQuantity())
                || !request.getInventoryId().equals(transaction.getInventory().getId())) {

            throw new BusinessException(
                    "Transaction type, quantity and inventory cannot be modified. Create a reversal transaction instead."
            );
        }

        transaction.setReferenceNumber(request.getReferenceNumber());
        transaction.setRemarks(request.getRemarks());

        InventoryTransaction updated =
                inventoryTransactionRepository.save(transaction);

        log.info("Inventory transaction updated successfully : {}",
                updated.getId());

        return inventoryTransactionMapper.toResponse(updated);
    }

    @Transactional
    @Override
    public void delete(Long id) {

        log.info("Deleting inventory transaction {}", id);

        InventoryTransaction transaction =
                findTransactionOrThrow(id);

        Inventory inventory = transaction.getInventory();

        reverseStockMovement(
                inventory,
                transaction.getTransactionType(),
                transaction.getQuantity()
        );

        inventory.setLastUpdated(LocalDateTime.now());

        inventoryRepository.save(inventory);

        inventoryTransactionRepository.delete(transaction);

        log.info("Inventory transaction deleted successfully : {}", id);
    }

    private void applyStockMovement(Inventory inventory,
                                    TransactionType type,
                                    Integer quantity) {

        int current = inventory.getQuantityAvailable() == null
                ? 0
                : inventory.getQuantityAvailable();

        switch (type) {

            case INBOUND, ADJUSTMENT ->
                    inventory.setQuantityAvailable(current + quantity);

            case OUTBOUND, TRANSFER -> {

                int updated = current - quantity;

                if (updated < 0) {
                    throw new BusinessException(
                            "Insufficient stock available."
                    );
                }

                inventory.setQuantityAvailable(updated);
            }
        }
    }

    private void reverseStockMovement(Inventory inventory,
                                      TransactionType type,
                                      Integer quantity) {

        int current = inventory.getQuantityAvailable() == null
                ? 0
                : inventory.getQuantityAvailable();

        switch (type) {

            case INBOUND, ADJUSTMENT -> {

                int updated = current - quantity;

                if (updated < 0) {

                    throw new BusinessException(
                            "Cannot reverse transaction because stock would become negative."
                    );
                }

                inventory.setQuantityAvailable(updated);
            }

            case OUTBOUND, TRANSFER ->
                    inventory.setQuantityAvailable(current + quantity);
        }
    }

    private InventoryTransaction findTransactionOrThrow(Long id) {

        return inventoryTransactionRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                RESOURCE_NAME,
                                id
                        ));
    }

    private Inventory findInventoryOrThrow(Long id) {

        return inventoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Inventory",
                                id
                        ));
    }
}