package com.factoryops.analytics.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductionDashboardResponse {

    private Integer totalMachines;

    private Integer operationalMachines;

    private Integer maintenancePending;

    private Integer runningBatches;

    private Integer completedBatches;

    private Double machineUtilization;
}