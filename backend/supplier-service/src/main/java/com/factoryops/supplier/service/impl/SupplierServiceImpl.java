package com.factoryops.supplier.service.impl;

import com.factoryops.supplier.dto.request.SupplierRequest;
import com.factoryops.supplier.dto.response.SupplierResponse;
import com.factoryops.supplier.entity.Supplier;
import com.factoryops.supplier.exception.BusinessException;
import com.factoryops.supplier.exception.ResourceNotFoundException;
import com.factoryops.supplier.mapper.SupplierMapper;
import com.factoryops.supplier.repository.SupplierRepository;
import com.factoryops.supplier.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SupplierServiceImpl implements SupplierService {

    private static final String RESOURCE_NAME = "Supplier";

    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;

    @Override
    public SupplierResponse create(SupplierRequest request) {
        supplierRepository.findByCode(request.getCode()).ifPresent(existing -> {
            throw new BusinessException("Supplier already exists with code: " + request.getCode());
        });

        Supplier supplier = supplierMapper.toEntity(request);

        LocalDateTime now = LocalDateTime.now();
        supplier.setCreatedAt(now);
        supplier.setUpdatedAt(now);

        Supplier saved = supplierRepository.save(supplier);
        return supplierMapper.toResponse(saved);
    }

    @Override
    public SupplierResponse getById(Long id) {
        Supplier supplier = findSupplierOrThrow(id);
        return supplierMapper.toResponse(supplier);
    }

    @Override
    public List<SupplierResponse> getAll() {
        return supplierMapper.toResponseList(supplierRepository.findAll());
    }

    @Override
    public SupplierResponse update(Long id, SupplierRequest request) {
        Supplier supplier = findSupplierOrThrow(id);

        supplierRepository.findByCode(request.getCode())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new BusinessException("Supplier already exists with code: " + request.getCode());
                });

        supplier.setCode(request.getCode());
        supplier.setName(request.getName());
        supplier.setContactPerson(request.getContactPerson());
        supplier.setEmail(request.getEmail());
        supplier.setPhone(request.getPhone());
        supplier.setAddress(request.getAddress());
        supplier.setCity(request.getCity());
        supplier.setCountry(request.getCountry());
        supplier.setStatus(request.getStatus());
        supplier.setRating(request.getRating());
        supplier.setUpdatedAt(LocalDateTime.now());

        Supplier updated = supplierRepository.save(supplier);
        return supplierMapper.toResponse(updated);
    }

    @Override
    public void delete(Long id) {
        Supplier supplier = findSupplierOrThrow(id);
        try {
            supplierRepository.delete(supplier);
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessException(
                    "Cannot delete supplier '" + supplier.getCode()
                            + "' because it is referenced by existing supplier performance records");
        }
    }

    private Supplier findSupplierOrThrow(Long id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, id));
    }
}
