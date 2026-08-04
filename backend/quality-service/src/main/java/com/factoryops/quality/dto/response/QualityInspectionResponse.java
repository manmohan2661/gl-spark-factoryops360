package com.factoryops.quality.dto.response;

import com.factoryops.quality.entity.InspectionResult;
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
public class QualityInspectionResponse {

    private Long id;

    private String inspectorName;

    private LocalDateTime inspectionDate;

    private InspectionResult result;

    private String remarks;

    private Long productionBatchId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
