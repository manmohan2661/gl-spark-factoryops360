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
public class SystemHealthResponse {

    private String overallStatus;

    private Double overallHealthScore;

    private Integer totalServices;

    private Integer healthyServices;

    private Integer unhealthyServices;

    private Long responseTime;

    private String lastHealthCheck;
}