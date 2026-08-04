package com.factoryops.supplier.service;

import com.factoryops.supplier.dto.request.SupplierPerformanceRequest;
import com.factoryops.supplier.dto.response.SupplierPerformanceResponse;

import java.util.List;

public interface SupplierPerformanceService {

    SupplierPerformanceResponse create(SupplierPerformanceRequest request);

    SupplierPerformanceResponse getById(Long id);

    List<SupplierPerformanceResponse> getAll();

    SupplierPerformanceResponse update(Long id, SupplierPerformanceRequest request);

    void delete(Long id);
}
