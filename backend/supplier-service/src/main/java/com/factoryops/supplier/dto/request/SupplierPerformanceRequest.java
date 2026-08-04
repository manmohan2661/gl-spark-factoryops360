package com.factoryops.supplier.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class SupplierPerformanceRequest {

    @NotBlank
    private String evaluationPeriod;

    private Double onTimeDeliveryRate;

    private Double qualityScore;

    private Double defectRate;

    private String remarks;

    private LocalDateTime evaluatedAt;

    @NotNull
    private Long supplierId;
}
