package com.factoryops.production.dto.request;

import com.factoryops.production.entity.ProductionOrderStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
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
public class ProductionOrderRequest {

    @NotBlank
    private String orderNumber;

    @NotBlank
    private String productName;

    @NotNull
    private Integer quantityOrdered;

    private Integer quantityProduced;

    @NotNull
    private ProductionOrderStatus status;

    private Integer priority;

    private LocalDate startDate;

    private LocalDate endDate;
}
