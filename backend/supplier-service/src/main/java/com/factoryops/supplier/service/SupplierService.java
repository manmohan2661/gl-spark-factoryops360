package com.factoryops.supplier.service;

import com.factoryops.supplier.dto.request.SupplierRequest;
import com.factoryops.supplier.dto.response.SupplierResponse;

import java.util.List;

public interface SupplierService {

    SupplierResponse create(SupplierRequest request);

    SupplierResponse getById(Long id);

    List<SupplierResponse> getAll();

    SupplierResponse update(Long id, SupplierRequest request);

    void delete(Long id);
}
