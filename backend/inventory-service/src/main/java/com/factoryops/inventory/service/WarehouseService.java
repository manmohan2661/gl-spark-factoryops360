package com.factoryops.inventory.service;

import com.factoryops.inventory.dto.request.WarehouseRequest;
import com.factoryops.inventory.dto.response.WarehouseResponse;

import java.util.List;

public interface WarehouseService {

    WarehouseResponse create(WarehouseRequest request);

    WarehouseResponse getById(Long id);

    List<WarehouseResponse> getAll();

    WarehouseResponse update(Long id, WarehouseRequest request);

    void delete(Long id);
}
