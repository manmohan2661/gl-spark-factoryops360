package com.factoryops.analytics.dto.response;

import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {


    private Long id;

    private Integer totalOrders;

    private Integer completedOrders;

    private Integer pendingOrders;

    private Integer totalProductionQuantity;

    private Integer totalMachines;

    private Integer operationalMachines;

    private Integer maintenancePending;

    private Double qualityPassRate;

    private Integer activeAlerts;

    private LocalDateTime generatedAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}