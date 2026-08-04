package com.factoryops.production.dto.response;

import com.factoryops.production.entity.BatchStatus;
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
public class ProductionBatchResponse {

    private Long id;

    private String batchNumber;

    private Integer quantity;

    private BatchStatus status;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Long productionOrderId;

    private Long machineId;

    private Long shiftId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
