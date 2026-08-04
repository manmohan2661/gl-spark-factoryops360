package com.factoryops.production.service;

import com.factoryops.production.dto.request.ProductionBatchRequest;
import com.factoryops.production.dto.response.ProductionBatchResponse;

import java.util.List;

public interface ProductionBatchService {

    ProductionBatchResponse create(ProductionBatchRequest request);

    ProductionBatchResponse getById(Long id);

    List<ProductionBatchResponse> getAll();

    ProductionBatchResponse update(Long id, ProductionBatchRequest request);

    void delete(Long id);
}
