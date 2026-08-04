package com.factoryops.production.dto.response;

import com.factoryops.production.entity.ProductionOrderStatus;
import java.time.LocalDate;
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
public class ProductionOrderResponse {

    private Long id;

    private String orderNumber;

    private String productName;

    private Integer quantityOrdered;

    private Integer quantityProduced;

    private ProductionOrderStatus status;

    private Integer priority;

    private LocalDate startDate;

    private LocalDate endDate;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
