package com.factoryops.supplier.config;

import com.factoryops.supplier.entity.Supplier;
import com.factoryops.supplier.entity.SupplierPerformance;
import com.factoryops.supplier.entity.SupplierStatus;
import com.factoryops.supplier.repository.SupplierPerformanceRepository;
import com.factoryops.supplier.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final SupplierRepository supplierRepository;
    private final SupplierPerformanceRepository supplierPerformanceRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (supplierRepository.count() > 0) {
            log.info("Supplier data already seeded. Skipping...");
            return;
        }

        log.info("Starting Supplier Data Seeding...");

        List<Supplier> suppliers = new ArrayList<>();
        
        // 1. High Performing Suppliers
        suppliers.add(createSupplier("SUP-001", "Apex Steel Corp", "Steel Supplier", 4.8));
        suppliers.add(createSupplier("SUP-002", "Nova Electronics", "Electronics Supplier", 4.9));
        suppliers.add(createSupplier("SUP-003", "Precision CNC Parts", "Machine Parts Supplier", 4.6));
        suppliers.add(createSupplier("SUP-004", "Global Polymer Tech", "Raw Material Supplier", 4.7));

        // 2. Average Suppliers
        suppliers.add(createSupplier("SUP-005", "Standard Metals", "Steel Supplier", 3.8));
        suppliers.add(createSupplier("SUP-006", "Circuit Solutions", "Electronics Supplier", 3.5));
        suppliers.add(createSupplier("SUP-007", "Indo Fasteners", "Machine Parts Supplier", 3.9));
        
        // 3. Low Performing / Delayed Suppliers
        suppliers.add(createSupplier("SUP-008", "Budget Plastics", "Raw Material Supplier", 2.5));
        suppliers.add(createSupplier("SUP-009", "Heavy Castings Co", "Steel Supplier", 2.8));
        suppliers.add(createSupplier("SUP-010", "Legacy Wires", "Electronics Supplier", 2.2));

        supplierRepository.saveAll(suppliers);

        List<SupplierPerformance> performances = new ArrayList<>();
        
        for (Supplier s : suppliers) {
            double onTime = 90.0;
            double defectRate = 2.0;
            double qualScore = 95.0;

            if (s.getRating() >= 4.5) {
                onTime = 98.5;
                defectRate = 0.5;
                qualScore = 98.0;
            } else if (s.getRating() >= 3.5) {
                onTime = 85.0;
                defectRate = 3.5;
                qualScore = 80.0;
            } else {
                onTime = 60.0; // Delayed supplier
                defectRate = 12.0; // High defect
                qualScore = 55.0;
            }

            performances.add(SupplierPerformance.builder()
                    .supplier(s)
                    .evaluationPeriod("2026-Q3")
                    .onTimeDeliveryRate(onTime)
                    .defectRate(defectRate)
                    .qualityScore(qualScore)
                    .evaluatedAt(LocalDateTime.now().minusDays(5))
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build());
        }

        supplierPerformanceRepository.saveAll(performances);
        log.info("Supplier Data Seeding Completed!");
    }

    private Supplier createSupplier(String code, String name, String category, Double rating) {
        return Supplier.builder()
                .code(code)
                .name(name)
                .contactPerson("John Doe")
                .email("contact@" + name.replaceAll("\\s+", "").toLowerCase() + ".com")
                .phone("+1-555-0100")
                .address("123 Industrial Park")
                .city("Metropolis")
                .country("USA")
                .status(SupplierStatus.ACTIVE)
                .rating(rating)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
