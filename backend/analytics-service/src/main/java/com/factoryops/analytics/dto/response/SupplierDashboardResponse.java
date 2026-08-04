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
public class SupplierDashboardResponse {

    private Integer totalSuppliers;

    private Integer activeSuppliers;

    private Double averageSupplierRating;

    private Integer pendingDeliveries;

    private Integer completedDeliveries;
}