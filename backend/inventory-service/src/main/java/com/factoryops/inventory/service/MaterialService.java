package com.factoryops.inventory.service;

import com.factoryops.inventory.dto.request.MaterialRequest;
import com.factoryops.inventory.dto.response.MaterialResponse;

import java.util.List;

public interface MaterialService {

    MaterialResponse create(MaterialRequest request);

    MaterialResponse getById(Long id);

    List<MaterialResponse> getAll();

    MaterialResponse update(Long id, MaterialRequest request);

    void delete(Long id);
}
