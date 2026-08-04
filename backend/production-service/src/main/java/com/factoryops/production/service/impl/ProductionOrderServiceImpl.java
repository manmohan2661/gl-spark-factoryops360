package com.factoryops.production.service.impl;

import com.factoryops.production.dto.request.ProductionOrderRequest;
import com.factoryops.production.dto.response.ProductionOrderResponse;
import com.factoryops.production.entity.ProductionOrder;
import com.factoryops.production.exception.BusinessException;
import com.factoryops.production.exception.ResourceNotFoundException;
import com.factoryops.production.mapper.ProductionOrderMapper;
import com.factoryops.production.repository.ProductionOrderRepository;
import com.factoryops.production.service.ProductionOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductionOrderServiceImpl implements ProductionOrderService {

    private static final String RESOURCE_NAME = "ProductionOrder";

    private final ProductionOrderRepository productionOrderRepository;
    private final ProductionOrderMapper productionOrderMapper;

    @Override
    public ProductionOrderResponse create(ProductionOrderRequest request) {

        productionOrderRepository.findByOrderNumber(request.getOrderNumber())
                .ifPresent(order -> {
                    throw new BusinessException(
                            "Production Order already exists with Order Number : "
                                    + request.getOrderNumber());
                });

        ProductionOrder order = productionOrderMapper.toEntity(request);

        if (order.getQuantityProduced() == null) {
            order.setQuantityProduced(0);
        }

        LocalDateTime now = LocalDateTime.now();

        order.setCreatedAt(now);
        order.setUpdatedAt(now);

        ProductionOrder saved = productionOrderRepository.save(order);

        return productionOrderMapper.toResponse(saved);
    }

    @Override
    public ProductionOrderResponse getById(Long id) {

        ProductionOrder order = findOrderOrThrow(id);

        return productionOrderMapper.toResponse(order);
    }

    @Override
    public List<ProductionOrderResponse> getAll() {

        return productionOrderMapper.toResponseList(
                productionOrderRepository.findAll()
        );
    }

    @Override
    public ProductionOrderResponse update(Long id,
                                          ProductionOrderRequest request) {

        ProductionOrder order = findOrderOrThrow(id);

        productionOrderRepository.findByOrderNumber(request.getOrderNumber())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new BusinessException(
                            "Production Order already exists with Order Number : "
                                    + request.getOrderNumber());
                });

        order.setOrderNumber(request.getOrderNumber());
        order.setProductName(request.getProductName());
        order.setQuantityOrdered(request.getQuantityOrdered());
        order.setQuantityProduced(request.getQuantityProduced());
        order.setStatus(request.getStatus());
        order.setPriority(request.getPriority());
        order.setStartDate(request.getStartDate());
        order.setEndDate(request.getEndDate());
        order.setUpdatedAt(LocalDateTime.now());

        ProductionOrder updated = productionOrderRepository.save(order);

        return productionOrderMapper.toResponse(updated);
    }

    @Override
    public void delete(Long id) {

        ProductionOrder order = findOrderOrThrow(id);

        try {
            productionOrderRepository.delete(order);
        }
        catch (DataIntegrityViolationException ex) {

            throw new BusinessException(
                    "Cannot delete Production Order "
                            + order.getOrderNumber()
                            + " because it is referenced by Production Batch."
            );
        }
    }

    private ProductionOrder findOrderOrThrow(Long id) {

        return productionOrderRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                RESOURCE_NAME,
                                id
                        ));
    }
}