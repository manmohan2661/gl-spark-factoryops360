package com.factoryops.supplier.service.impl;

import com.factoryops.supplier.dto.request.SupplierPerformanceRequest;
import com.factoryops.supplier.dto.response.SupplierPerformanceResponse;
import com.factoryops.supplier.entity.Supplier;
import com.factoryops.supplier.entity.SupplierPerformance;
import com.factoryops.supplier.exception.BusinessException;
import com.factoryops.supplier.exception.ResourceNotFoundException;
import com.factoryops.supplier.mapper.SupplierPerformanceMapper;
import com.factoryops.supplier.repository.SupplierPerformanceRepository;
import com.factoryops.supplier.repository.SupplierRepository;
import com.factoryops.supplier.service.SupplierPerformanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SupplierPerformanceServiceImpl implements SupplierPerformanceService {

    private static final String RESOURCE_NAME = "SupplierPerformance";

    private final SupplierPerformanceRepository supplierPerformanceRepository;
    private final SupplierPerformanceMapper supplierPerformanceMapper;
    private final SupplierRepository supplierRepository;

    @Transactional
    @Override
    public SupplierPerformanceResponse create(SupplierPerformanceRequest request) {

        log.info("Creating supplier performance for supplier {}",
                request.getSupplierId());

        Supplier supplier = findSupplierOrThrow(request.getSupplierId());

        validateRates(request);

        SupplierPerformance performance =
                supplierPerformanceMapper.toEntity(request);

        performance.setSupplier(supplier);

        LocalDateTime now = LocalDateTime.now();

        if (performance.getEvaluatedAt() == null) {
            performance.setEvaluatedAt(now);
        }

        performance.setCreatedAt(now);
        performance.setUpdatedAt(now);

        SupplierPerformance saved =
                supplierPerformanceRepository.save(performance);

        log.info("Supplier performance created successfully : {}",
                saved.getId());

        return supplierPerformanceMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public SupplierPerformanceResponse getById(Long id) {

        log.debug("Fetching supplier performance {}", id);

        SupplierPerformance performance = findPerformanceOrThrow(id);

        return supplierPerformanceMapper.toResponse(performance);
    }

    @Transactional(readOnly = true)
    @Override
    public List<SupplierPerformanceResponse> getAll() {

        log.debug("Fetching all supplier performance records");

        return supplierPerformanceMapper.toResponseList(
                supplierPerformanceRepository.findAll()
        );
    }

    @Transactional
    @Override
    public SupplierPerformanceResponse update(Long id,
                                              SupplierPerformanceRequest request) {

        log.info("Updating supplier performance {}", id);

        SupplierPerformance performance =
                findPerformanceOrThrow(id);

        Supplier supplier =
                findSupplierOrThrow(request.getSupplierId());

        validateRates(request);

        performance.setSupplier(supplier);
        performance.setEvaluationPeriod(request.getEvaluationPeriod());
        performance.setOnTimeDeliveryRate(request.getOnTimeDeliveryRate());
        performance.setQualityScore(request.getQualityScore());
        performance.setDefectRate(request.getDefectRate());
        performance.setRemarks(request.getRemarks());
        performance.setEvaluatedAt(request.getEvaluatedAt());
        performance.setUpdatedAt(LocalDateTime.now());

        SupplierPerformance updated =
                supplierPerformanceRepository.save(performance);

        log.info("Supplier performance updated successfully : {}",
                updated.getId());

        return supplierPerformanceMapper.toResponse(updated);
    }

    @Transactional
    @Override
    public void delete(Long id) {

        log.info("Deleting supplier performance {}", id);

        SupplierPerformance performance =
                findPerformanceOrThrow(id);

        supplierPerformanceRepository.delete(performance);

        log.info("Supplier performance deleted successfully : {}", id);
    }

    private void validateRates(SupplierPerformanceRequest request) {

        rejectIfOutOfPercentageRange(
                request.getOnTimeDeliveryRate(),
                "onTimeDeliveryRate"
        );

        rejectIfOutOfPercentageRange(
                request.getQualityScore(),
                "qualityScore"
        );

        rejectIfOutOfPercentageRange(
                request.getDefectRate(),
                "defectRate"
        );
    }

    private void rejectIfOutOfPercentageRange(Double value,
                                              String fieldName) {

        if (value != null && (value < 0.0 || value > 100.0)) {

            throw new BusinessException(
                    fieldName + " must be between 0 and 100"
            );
        }
    }

    private SupplierPerformance findPerformanceOrThrow(Long id) {

        return supplierPerformanceRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                RESOURCE_NAME,
                                id
                        ));
    }

    private Supplier findSupplierOrThrow(Long supplierId) {

        return supplierRepository.findById(supplierId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Supplier",
                                supplierId
                        ));
    }
}