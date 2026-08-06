package com.factoryops.analytics.config;

import com.factoryops.analytics.entity.Alert;
import com.factoryops.analytics.entity.AlertSeverity;
import com.factoryops.analytics.repository.AlertRepository;
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

    private final AlertRepository alertRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (alertRepository.count() > 0) {
            log.info("Analytics Alert data already seeded. Skipping...");
            return;
        }

        log.info("Starting Analytics Data Seeding...");

        List<Alert> alerts = new ArrayList<>();

        // CRITICAL ALERTS
        alerts.add(createAlert("Machine Breakdown Detected", "Robotic Arm X2 has unexpectedly stopped.", AlertSeverity.CRITICAL, "production-service"));
        alerts.add(createAlert("Critical Quality Failure", "Batch #18 failed inspection due to severe tolerance mismatch.", AlertSeverity.CRITICAL, "quality-service"));
        alerts.add(createAlert("Database Latency", "Inventory DB connection timed out.", AlertSeverity.CRITICAL, "system"));

        // HIGH ALERTS
        alerts.add(createAlert("Production Delay", "Order PO-2026-0020 is on hold.", AlertSeverity.HIGH, "production-service"));
        alerts.add(createAlert("Low Inventory", "Material MAT-001 has fallen below reorder threshold.", AlertSeverity.HIGH, "inventory-service"));
        alerts.add(createAlert("Low Inventory", "Material MAT-002 is critically low.", AlertSeverity.HIGH, "inventory-service"));
        alerts.add(createAlert("Low Inventory", "Material MAT-003 is out of stock.", AlertSeverity.HIGH, "inventory-service"));
        alerts.add(createAlert("Machine Under Maintenance", "Milling Machine B1 is offline.", AlertSeverity.HIGH, "production-service"));
        alerts.add(createAlert("System Warning", "High memory usage detected on node 2.", AlertSeverity.HIGH, "system"));
        alerts.add(createAlert("Order On Hold", "Order PO-2026-0021 blocked by missing parts.", AlertSeverity.HIGH, "production-service"));

        // MEDIUM / LOW ALERTS
        for (int i = 1; i <= 10; i++) {
            alerts.add(createAlert("Supplier Delay Warning", "Supplier delivery expected tomorrow may be delayed.", AlertSeverity.MEDIUM, "supplier-service"));
        }

        alertRepository.saveAll(alerts);

        log.info("Analytics Data Seeding Completed!");
    }

    private Alert createAlert(String title, String message, AlertSeverity severity, String source) {
        return Alert.builder()
                .title(title)
                .message(message)
                .severity(severity)
                .sourceService(source)
                .triggeredAt(LocalDateTime.now().minusHours(severity == AlertSeverity.CRITICAL ? 1 : 5))
                .acknowledged(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
