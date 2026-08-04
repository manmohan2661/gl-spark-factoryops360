package com.factoryops.production.repository;

import com.factoryops.production.entity.ProductionOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductionOrderRepository extends JpaRepository<ProductionOrder, Long> {

    Optional<ProductionOrder> findByOrderNumber(String orderNumber);

    boolean existsByOrderNumber(String orderNumber);
}