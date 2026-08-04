package com.factoryops.quality.dto.request;

import com.factoryops.quality.entity.DefectSeverity;
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
public class DefectRequest {

    @NotBlank
    private String defectType;

    @NotNull
    private DefectSeverity severity;

    private String description;

    private LocalDateTime reportedDate;

    private Boolean resolved;

    @NotNull
    private Long qualityInspectionId;
}
