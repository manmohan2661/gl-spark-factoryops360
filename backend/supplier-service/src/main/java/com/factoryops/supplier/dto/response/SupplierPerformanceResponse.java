package com.factoryops.supplier.dto.response;

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
public class SupplierPerformanceResponse {

    private Long id;

    private String evaluationPeriod;

    private Double onTimeDeliveryRate;

    private Double qualityScore;

    private Double defectRate;

    private String remarks;

    private LocalDateTime evaluatedAt;

    private Long supplierId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
