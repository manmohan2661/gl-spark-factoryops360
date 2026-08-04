package com.factoryops.inventory.service;

import com.factoryops.inventory.dto.request.InventoryTransactionRequest;
import com.factoryops.inventory.dto.response.InventoryTransactionResponse;

import java.util.List;

public interface InventoryTransactionService {

    InventoryTransactionResponse create(InventoryTransactionRequest request);

    InventoryTransactionResponse getById(Long id);

    List<InventoryTransactionResponse> getAll();

    InventoryTransactionResponse update(Long id, InventoryTransactionRequest request);

    void delete(Long id);
}
