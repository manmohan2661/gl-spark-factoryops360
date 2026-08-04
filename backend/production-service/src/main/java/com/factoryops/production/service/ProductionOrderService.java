package com.factoryops.production.service;

import com.factoryops.production.dto.request.ProductionOrderRequest;
import com.factoryops.production.dto.response.ProductionOrderResponse;

import java.util.List;

public interface ProductionOrderService {

    ProductionOrderResponse create(ProductionOrderRequest request);

    ProductionOrderResponse getById(Long id);

    List<ProductionOrderResponse> getAll();

    ProductionOrderResponse update(Long id, ProductionOrderRequest request);

    void delete(Long id);
}
