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
public class InventoryDashboardResponse {

    private Integer totalMaterials;

    private Integer lowStockMaterials;

    private Integer outOfStockMaterials;

    private Double inventoryValue;

    private Integer totalWarehouses;
}