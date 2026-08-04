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
public class QualityDashboardResponse {

    private Integer totalInspections;

    private Integer passedInspections;

    private Integer failedInspections;

    private Double qualityPassRate;

    private Integer totalDefects;
}