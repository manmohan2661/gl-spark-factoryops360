package com.factoryops.analytics.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {

    private DashboardSummaryResponse summary;

    private ProductionDashboardResponse production;

    private InventoryDashboardResponse inventory;

    private SupplierDashboardResponse supplier;

    private QualityDashboardResponse quality;

    private AlertDashboardResponse alerts;

    private SystemHealthResponse systemHealth;
}