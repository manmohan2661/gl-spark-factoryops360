package com.factoryops.analytics.service.impl;

import com.factoryops.analytics.client.InventoryClient;
import com.factoryops.analytics.client.ProductionClient;
import com.factoryops.analytics.client.QualityClient;
import com.factoryops.analytics.client.SupplierClient;
import com.factoryops.analytics.dto.response.*;
import com.factoryops.analytics.entity.AlertSeverity;
import com.factoryops.analytics.entity.NotificationStatus;
import com.factoryops.analytics.repository.AlertRepository;
import com.factoryops.analytics.repository.AuditLogRepository;
import com.factoryops.analytics.repository.NotificationRepository;
import com.factoryops.analytics.service.DashboardService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private static final int TOTAL_SERVICES = 7;
    private static final long DEFAULT_RESPONSE_TIME = 25L;

    private final AlertRepository alertRepository;
    private final NotificationRepository notificationRepository;
    private final AuditLogRepository auditLogRepository;

    private final ProductionClient productionClient;
    private final InventoryClient inventoryClient;
    private final QualityClient qualityClient;
    private final SupplierClient supplierClient;

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
                .totalOrders(30)
                .completedOrders(18)
                .pendingOrders(12)
                .totalProductionQuantity(45000)
                .overallHealthScore(calculateHealthScore())
                .generatedAt(LocalDateTime.now())
                .build();
    }

    private ProductionDashboardResponse buildProduction() {
        int totalMachines = 0;
        int operationalMachines = 0;
        int maintenancePending = 0;
        int runningBatches = 0;
        int completedBatches = 0;
        double machineUtilization = 0.0;

        try {
            ApiResponse<List<JsonNode>> machinesResponse = productionClient.getAllMachines();
            if (machinesResponse != null && machinesResponse.getData() != null) {
                totalMachines = machinesResponse.getData().size();
                for (JsonNode machine : machinesResponse.getData()) {
                    String status = machine.path("status").asText();
                    if ("OPERATIONAL".equals(status)) {
                        operationalMachines++;
                    } else if ("UNDER_MAINTENANCE".equals(status)) {
                        maintenancePending++;
                    }
                }
                if (totalMachines > 0) {
                    machineUtilization = (double) operationalMachines / totalMachines * 100.0;
                }
            }

            ApiResponse<List<JsonNode>> ordersResponse = productionClient.getAllProductionOrders();
            if (ordersResponse != null && ordersResponse.getData() != null) {
                for (JsonNode order : ordersResponse.getData()) {
                    String status = order.path("status").asText();
                    if ("IN_PROGRESS".equals(status)) {
                        runningBatches++;
                    } else if ("COMPLETED".equals(status)) {
                        completedBatches++;
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to fetch production data: {}", e.getMessage());
        }

        return ProductionDashboardResponse.builder()
                .totalMachines(totalMachines)
                .operationalMachines(operationalMachines)
                .maintenancePending(maintenancePending)
                .runningBatches(runningBatches)
                .completedBatches(completedBatches)
                .machineUtilization(machineUtilization)
                .build();
    }

    private InventoryDashboardResponse buildInventory() {
        int totalMaterials = 0;
        int lowStock = 0;
        int outOfStock = 0;
        int totalWarehouses = 0;

        try {
            ApiResponse<List<JsonNode>> materialsResponse = inventoryClient.getAllMaterials();
            if (materialsResponse != null && materialsResponse.getData() != null) {
                totalMaterials = materialsResponse.getData().size();
            }

            ApiResponse<List<JsonNode>> invResponse = inventoryClient.getAllInventories();
            if (invResponse != null && invResponse.getData() != null) {
                for (JsonNode inv : invResponse.getData()) {
                    int qty = inv.path("quantityAvailable").asInt();
                    if (qty == 0) {
                        outOfStock++;
                    } else if (qty < 100) {
                        lowStock++;
                    }
                }
            }
            totalWarehouses = 5; 
        } catch (Exception e) {
            log.error("Failed to fetch inventory data: {}", e.getMessage());
        }

        return InventoryDashboardResponse.builder()
                .totalMaterials(totalMaterials)
                .lowStockMaterials(lowStock)
                .outOfStockMaterials(outOfStock)
                .inventoryValue(500000.0)
                .totalWarehouses(totalWarehouses)
                .build();
    }

    private SupplierDashboardResponse buildSupplier() {
        int totalSuppliers = 0;
        int activeSuppliers = 0;
        double averageSupplierRating = 0.0;

        try {
            ApiResponse<List<JsonNode>> suppliersResponse = supplierClient.getAllSuppliers();
            if (suppliersResponse != null && suppliersResponse.getData() != null) {
                totalSuppliers = suppliersResponse.getData().size();
                activeSuppliers = totalSuppliers;
            }

            ApiResponse<List<JsonNode>> perfResponse = supplierClient.getAllSupplierPerformances();
            if (perfResponse != null && perfResponse.getData() != null) {
                double totalScore = 0;
                for (JsonNode perf : perfResponse.getData()) {
                    totalScore += perf.path("qualityScore").asDouble();
                }
                if (perfResponse.getData().size() > 0) {
                    averageSupplierRating = totalScore / perfResponse.getData().size();
                }
            }
        } catch (Exception e) {
            log.error("Failed to fetch supplier data: {}", e.getMessage());
        }

        return SupplierDashboardResponse.builder()
                .totalSuppliers(totalSuppliers)
                .activeSuppliers(activeSuppliers)
                .averageSupplierRating(averageSupplierRating)
                .pendingDeliveries(3)
                .completedDeliveries(15)
                .build();
    }

    private QualityDashboardResponse buildQuality() {
        int totalInspections = 0;
        int passedInspections = 0;
        int failedInspections = 0;
        double qualityPassRate = 0.0;
        int totalDefects = 0;

        try {
            ApiResponse<List<JsonNode>> inspResponse = qualityClient.getAllInspections();
            if (inspResponse != null && inspResponse.getData() != null) {
                totalInspections = inspResponse.getData().size();
                for (JsonNode insp : inspResponse.getData()) {
                    String result = insp.path("result").asText();
                    if ("PASS".equals(result)) passedInspections++;
                    if ("FAIL".equals(result)) failedInspections++;
                }
                if (totalInspections > 0) {
                    qualityPassRate = (double) passedInspections / totalInspections * 100.0;
                }
            }

            ApiResponse<List<JsonNode>> defectsResponse = qualityClient.getAllDefects();
            if (defectsResponse != null && defectsResponse.getData() != null) {
                totalDefects = defectsResponse.getData().size();
            }
        } catch (Exception e) {
            log.error("Failed to fetch quality data: {}", e.getMessage());
        }

        return QualityDashboardResponse.builder()
                .totalInspections(totalInspections)
                .passedInspections(passedInspections)
                .failedInspections(failedInspections)
                .qualityPassRate(qualityPassRate)
                .totalDefects(totalDefects)
                .build();
    }

    private AlertDashboardResponse buildAlerts() {

        long activeAlerts = alertRepository.countByAcknowledgedFalse();
        long acknowledgedAlerts = alertRepository.countByAcknowledgedTrue();

        long criticalAlerts = alertRepository.countBySeverity(AlertSeverity.CRITICAL);
        long warningAlerts = alertRepository.countBySeverity(AlertSeverity.HIGH);

        return AlertDashboardResponse.builder()
                .activeAlerts((int) activeAlerts)
                .criticalAlerts((int) criticalAlerts)
                .warningAlerts((int) warningAlerts)
                .acknowledgedAlerts((int) acknowledgedAlerts)
                .unAcknowledgedAlerts((int) activeAlerts)
                .build();
    }

    private SystemHealthResponse buildSystemHealth() {
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
        if (activeAlerts == 0) return 100.0;
        if (activeAlerts <= 5) return 95.0;
        if (activeAlerts <= 10) return 85.0;
        return 70.0;
    }

    private String determineStatus() {
        double score = calculateHealthScore();
        if (score >= 90) return "HEALTHY";
        if (score >= 70) return "WARNING";
        return "CRITICAL";
    }

}