package com.factoryops.quality.dto.request;

import com.factoryops.quality.entity.InspectionResult;
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
public class QualityInspectionRequest {

    @NotBlank
    private String inspectorName;

    private LocalDateTime inspectionDate;

    @NotNull
    private InspectionResult result;

    private String remarks;

    @NotNull
    private Long productionBatchId;
}
