package com.factoryops.analytics.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryResponse {

    private Integer totalOrders;

    private Integer completedOrders;

    private Integer pendingOrders;

    private Integer totalProductionQuantity;

    private Double overallHealthScore;

    private LocalDateTime generatedAt;
}