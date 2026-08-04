package com.factoryops.analytics.service.impl;

import com.factoryops.analytics.dto.response.*;
import com.factoryops.analytics.entity.AlertSeverity;
import com.factoryops.analytics.entity.NotificationStatus;
import com.factoryops.analytics.repository.AlertRepository;
import com.factoryops.analytics.repository.AuditLogRepository;
import com.factoryops.analytics.repository.NotificationRepository;
import com.factoryops.analytics.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private static final int TOTAL_SERVICES = 7;
    private static final long DEFAULT_RESPONSE_TIME = 25L;

    private final AlertRepository alertRepository;
    private final NotificationRepository notificationRepository;
    private final AuditLogRepository auditLogRepository;

    @Override
    public DashboardResponse getDashboard() {

        log.info("Generating analytics dashboard");

        DashboardResponse response = DashboardResponse.builder()
                .summary(buildSummary())
                .production(buildProduction())
                .inventory(buildInventory())
                .supplier(buildSupplier())
                .quality(buildQuality())
                .alerts(buildAlerts())
                .systemHealth(buildSystemHealth())
                .build();

        log.info("Analytics dashboard generated successfully");

        return response;
    }

    private DashboardSummaryResponse buildSummary() {

        return DashboardSummaryResponse.builder()
                .totalOrders(0)
                .completedOrders(0)
                .pendingOrders(0)
                .totalProductionQuantity(0)
                .overallHealthScore(calculateHealthScore())
                .generatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * TODO
     * Replace hardcoded production metrics using Production Service
     * through OpenFeign once inter-service communication is added.
     */
    private ProductionDashboardResponse buildProduction() {

        return ProductionDashboardResponse.builder()
                .totalMachines(0)
                .operationalMachines(0)
                .maintenancePending(0)
                .runningBatches(0)
                .completedBatches(0)
                .machineUtilization(0.0)
                .build();
    }

    /**
     * TODO
     * Replace hardcoded inventory metrics using Inventory Service
     * through OpenFeign.
     */
    private InventoryDashboardResponse buildInventory() {

        return InventoryDashboardResponse.builder()
                .totalMaterials(0)
                .lowStockMaterials(0)
                .outOfStockMaterials(0)
                .inventoryValue(0.0)
                .totalWarehouses(0)
                .build();
    }

    /**
     * TODO
     * Replace hardcoded supplier metrics using Supplier Service
     * through OpenFeign.
     */
    private SupplierDashboardResponse buildSupplier() {

        return SupplierDashboardResponse.builder()
                .totalSuppliers(0)
                .activeSuppliers(0)
                .averageSupplierRating(0.0)
                .pendingDeliveries(0)
                .completedDeliveries(0)
                .build();
    }

    private QualityDashboardResponse buildQuality() {

        return QualityDashboardResponse.builder()
                .totalInspections(0)
                .passedInspections(0)
                .failedInspections(0)
                .qualityPassRate(0.0)
                .totalDefects(0)
                .build();
    }

    private AlertDashboardResponse buildAlerts() {

        long activeAlerts = alertRepository.countByAcknowledgedFalse();
        long acknowledgedAlerts = alertRepository.countByAcknowledgedTrue();

        long criticalAlerts =
                alertRepository.countBySeverity(AlertSeverity.CRITICAL);

        long warningAlerts =
                alertRepository.countBySeverity(AlertSeverity.HIGH);

        long sentNotifications =
                notificationRepository.countByStatus(NotificationStatus.SENT);

        long failedNotifications =
                notificationRepository.countByStatus(NotificationStatus.FAILED);

        log.debug("Sent Notifications : {}", sentNotifications);
        log.debug("Failed Notifications : {}", failedNotifications);

        return AlertDashboardResponse.builder()
                .activeAlerts((int) activeAlerts)
                .criticalAlerts((int) criticalAlerts)
                .warningAlerts((int) warningAlerts)
                .acknowledgedAlerts((int) acknowledgedAlerts)
                .unAcknowledgedAlerts((int) activeAlerts)
                .build();
    }

    private SystemHealthResponse buildSystemHealth() {

        long totalAuditLogs = auditLogRepository.count();

        log.debug("Total Audit Logs : {}", totalAuditLogs);

        return SystemHealthResponse.builder()
                .overallStatus(determineStatus())
                .overallHealthScore(calculateHealthScore())
                .totalServices(TOTAL_SERVICES)
                .healthyServices(TOTAL_SERVICES)
                .unhealthyServices(0)
                .responseTime(DEFAULT_RESPONSE_TIME)
                .lastHealthCheck(LocalDateTime.now().toString())
                .build();
    }

    private double calculateHealthScore() {

        long activeAlerts = alertRepository.countByAcknowledgedFalse();

        if (activeAlerts == 0) {
            return 100.0;
        }

        if (activeAlerts <= 5) {
            return 95.0;
        }

        if (activeAlerts <= 10) {
            return 85.0;
        }

        return 70.0;
    }

    private String determineStatus() {

        double score = calculateHealthScore();

        if (score >= 90) {
            return "HEALTHY";
        }

        if (score >= 70) {
            return "WARNING";
        }

        return "CRITICAL";
    }

}