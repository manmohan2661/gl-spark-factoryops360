package com.factoryops.production.dto.request;

import com.factoryops.production.entity.BatchStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
public class ProductionBatchRequest {

    @NotBlank
    private String batchNumber;

    @NotNull
    @Positive
    private Integer quantity;

    @NotNull
    private BatchStatus status;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @NotNull
    private Long productionOrderId;

    private Long machineId;

    private Long shiftId;
}
