package com.factoryops.inventory.service;

import com.factoryops.inventory.dto.request.InventoryRequest;
import com.factoryops.inventory.dto.response.InventoryResponse;

import java.util.List;

public interface InventoryService {

    InventoryResponse create(InventoryRequest request);

    InventoryResponse getById(Long id);

    List<InventoryResponse> getAll();

    InventoryResponse update(Long id, InventoryRequest request);

    void delete(Long id);
}
