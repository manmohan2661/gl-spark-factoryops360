package com.factoryops.inventory.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryRequest {

    @NotNull(message = "Available quantity is required")
    @Min(value = 0, message = "Available quantity cannot be negative")
    private Integer quantityAvailable;

    @Builder.Default
    @Min(value = 0, message = "Reserved quantity cannot be negative")
    private Integer quantityReserved = 0;

    @NotNull(message = "Material ID is required")
    private Long materialId;
    @NotNull(message = "Warehouse ID is required")
    private Long warehouseId;
}