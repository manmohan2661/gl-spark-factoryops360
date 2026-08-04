package com.factoryops.inventory.dto.response;

import java.time.LocalDateTime;
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
public class InventoryResponse {

    private Long id;

    private Integer quantityAvailable;

    private Integer quantityReserved;

    private LocalDateTime lastUpdated;

    private Long materialId;

    private Long warehouseId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
