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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryTransactionServiceImpl implements InventoryTransactionService {

    private static final String RESOURCE_NAME = "InventoryTransaction";

    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final InventoryTransactionMapper inventoryTransactionMapper;
    private final InventoryRepository inventoryRepository;

    @Override
    @Transactional
    public InventoryTransactionResponse create(InventoryTransactionRequest request) {

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

        return inventoryTransactionMapper.toResponse(saved);
    }

    @Override
    public InventoryTransactionResponse getById(Long id) {

        return inventoryTransactionMapper.toResponse(
                findTransactionOrThrow(id)
        );
    }

    @Override
    public List<InventoryTransactionResponse> getAll() {

        return inventoryTransactionMapper.toResponseList(
                inventoryTransactionRepository.findAll()
        );
    }

    @Override
    @Transactional
    public InventoryTransactionResponse update(Long id,
                                               InventoryTransactionRequest request) {

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

        return inventoryTransactionMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void delete(Long id) {

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
    }

    private void applyStockMovement(Inventory inventory,
                                    TransactionType type,
                                    Integer quantity) {

        int current =
                inventory.getQuantityAvailable() == null
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

        int current =
                inventory.getQuantityAvailable() == null
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
                        new ResourceNotFoundException(RESOURCE_NAME, id));
    }

    private Inventory findInventoryOrThrow(Long id) {

        return inventoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Inventory", id));
    }
}