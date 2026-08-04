package com.factoryops.supplier.repository;

import com.factoryops.supplier.entity.SupplierPerformance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierPerformanceRepository extends JpaRepository<SupplierPerformance, Long> {
}
