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
public class AlertDashboardResponse {

    private Integer activeAlerts;

    private Integer criticalAlerts;

    private Integer warningAlerts;

    private Integer acknowledgedAlerts;

    private Integer unAcknowledgedAlerts;
}