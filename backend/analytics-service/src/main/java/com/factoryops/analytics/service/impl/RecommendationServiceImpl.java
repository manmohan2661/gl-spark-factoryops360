package com.factoryops.analytics.service.impl;

import com.factoryops.analytics.client.InventoryClient;
import com.factoryops.analytics.client.ProductionClient;
import com.factoryops.analytics.client.QualityClient;
import com.factoryops.analytics.client.SupplierClient;
import com.factoryops.analytics.dto.response.ApiResponse;
import com.factoryops.analytics.dto.response.RecommendationResponse;
import com.factoryops.analytics.service.RecommendationService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final InventoryClient inventoryClient;
    private final ProductionClient productionClient;
    private final QualityClient qualityClient;
    private final SupplierClient supplierClient;

    @Override
    public List<RecommendationResponse> getRecommendations() {
        List<RecommendationResponse> recommendations = new ArrayList<>();
        String role = getCurrentRole();
        
        log.info("Generating recommendations for role: {}", role);

        if (role.equals("ADMIN") || role.equals("INVENTORY_MANAGER")) {
            recommendations.addAll(analyzeInventory());
        }
        if (role.equals("ADMIN") || role.equals("PRODUCTION_MANAGER")) {
            recommendations.addAll(analyzeProduction());
        }
        if (role.equals("ADMIN") || role.equals("QUALITY_INSPECTOR")) {
            recommendations.addAll(analyzeQuality());
        }
        if (role.equals("ADMIN") || role.equals("SUPPLIER_MANAGER")) {
            recommendations.addAll(analyzeSupplier());
        }

        // Sort by risk score descending
        return recommendations.stream()
                .sorted(Comparator.comparing(RecommendationResponse::getRiskScore).reversed())
                .limit(5) // Top 5
                .collect(Collectors.toList());
    }

    private String getCurrentRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            return auth.getAuthorities().stream()
                    .findFirst()
                    .map(a -> a.getAuthority().replace("ROLE_", ""))
                    .orElse("");
        }
        return "";
    }

    private List<RecommendationResponse> analyzeInventory() {
        List<RecommendationResponse> recs = new ArrayList<>();
        try {
            ApiResponse<List<JsonNode>> response = inventoryClient.getAllInventories();
            if (response != null && response.getData() != null) {
                // To properly evaluate reorder thresholds, we would map materials to inventories. 
                // Since this engine is acting statelessly, we rely on the quantity check.
                // Assuming standard normal threshold logic for now if we can't join easily:
                for (JsonNode inv : response.getData()) {
                    int qty = inv.has("quantityAvailable") ? inv.get("quantityAvailable").asInt() : 0;
                    // Mocking a standard threshold or pulling from node if available
                    // For US-06 MVP, if quantity is critically low (e.g. < 50):
                    if (qty < 50) {
                        recs.add(RecommendationResponse.builder()
                                .id(UUID.randomUUID().toString())
                                .title("LOW STOCK RISK")
                                .category("INVENTORY")
                                .severity(qty < 20 ? "CRITICAL" : "HIGH")
                                .riskScore(qty < 20 ? 90 : 60)
                                .description("Material stock is running critically low: " + qty + " units remaining.")
                                .affectedModule("Inventory")
                                .recommendedAction("Trigger emergency reorder immediately.")
                                .createdAt(LocalDateTime.now())
                                .build());
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to analyze inventory: {}", e.getMessage());
        }
        return recs;
    }

    private List<RecommendationResponse> analyzeProduction() {
        List<RecommendationResponse> recs = new ArrayList<>();
        try {
            ApiResponse<List<JsonNode>> machines = productionClient.getAllMachines();
            if (machines != null && machines.getData() != null) {
                for (JsonNode m : machines.getData()) {
                    String status = m.has("status") ? m.get("status").asText() : "";
                    String name = m.has("name") ? m.get("name").asText() : "Unknown Machine";
                    if ("BREAKDOWN".equalsIgnoreCase(status)) {
                        recs.add(RecommendationResponse.builder()
                                .id(UUID.randomUUID().toString())
                                .title("Machine Breakdown Risk")
                                .category("PRODUCTION")
                                .severity("CRITICAL")
                                .riskScore(95)
                                .description("Machine " + name + " has experienced a breakdown.")
                                .affectedModule("Production")
                                .recommendedAction("Dispatch maintenance team to " + name + " immediately.")
                                .createdAt(LocalDateTime.now())
                                .build());
                    }
                }
            }

            ApiResponse<List<JsonNode>> orders = productionClient.getAllProductionOrders();
            if (orders != null && orders.getData() != null) {
                for (JsonNode o : orders.getData()) {
                    String status = o.has("status") ? o.get("status").asText() : "";
                    String orderNo = o.has("orderNumber") ? o.get("orderNumber").asText() : "Unknown";
                    if ("ON_HOLD".equalsIgnoreCase(status) || "CANCELLED".equalsIgnoreCase(status)) {
                        recs.add(RecommendationResponse.builder()
                                .id(UUID.randomUUID().toString())
                                .title("Production Order Delayed")
                                .category("PRODUCTION")
                                .severity("HIGH")
                                .riskScore(80)
                                .description("Production order " + orderNo + " is currently on hold.")
                                .affectedModule("Production")
                                .recommendedAction("Investigate blockers for order " + orderNo + " and resolve.")
                                .createdAt(LocalDateTime.now())
                                .build());
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to analyze production: {}", e.getMessage());
        }
        return recs;
    }

    private List<RecommendationResponse> analyzeQuality() {
        List<RecommendationResponse> recs = new ArrayList<>();
        try {
            ApiResponse<List<JsonNode>> defects = qualityClient.getAllDefects();
            if (defects != null && defects.getData() != null) {
                for (JsonNode d : defects.getData()) {
                    String severity = d.has("severity") ? d.get("severity").asText() : "";
                    boolean resolved = d.has("resolved") && d.get("resolved").asBoolean();
                    if ("CRITICAL".equalsIgnoreCase(severity) && !resolved) {
                        recs.add(RecommendationResponse.builder()
                                .id(UUID.randomUUID().toString())
                                .title("Critical Quality Defect")
                                .category("QUALITY")
                                .severity("CRITICAL")
                                .riskScore(90)
                                .description("Unresolved critical defect found during inspection.")
                                .affectedModule("Quality")
                                .recommendedAction("Quarantine affected batch and perform root cause analysis.")
                                .createdAt(LocalDateTime.now())
                                .build());
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to analyze quality: {}", e.getMessage());
        }
        return recs;
    }

    private List<RecommendationResponse> analyzeSupplier() {
        List<RecommendationResponse> recs = new ArrayList<>();
        try {
            ApiResponse<List<JsonNode>> perfs = supplierClient.getAllSupplierPerformances();
            if (perfs != null && perfs.getData() != null) {
                for (JsonNode p : perfs.getData()) {
                    double onTime = p.has("onTimeDeliveryRate") ? p.get("onTimeDeliveryRate").asDouble() : 100.0;
                    double defectRate = p.has("defectRate") ? p.get("defectRate").asDouble() : 0.0;
                    if (onTime < 75.0 || defectRate > 10.0) {
                        recs.add(RecommendationResponse.builder()
                                .id(UUID.randomUUID().toString())
                                .title("Supplier Delivery / Quality Failure")
                                .category("SUPPLIER")
                                .severity("HIGH")
                                .riskScore(75)
                                .description("Supplier performance dropped below acceptable thresholds.")
                                .affectedModule("Supplier")
                                .recommendedAction("Engage alternate supplier or schedule immediate review.")
                                .createdAt(LocalDateTime.now())
                                .build());
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to analyze supplier: {}", e.getMessage());
        }
        return recs;
    }
}
