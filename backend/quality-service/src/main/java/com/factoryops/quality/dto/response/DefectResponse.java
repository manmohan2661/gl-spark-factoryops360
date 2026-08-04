package com.factoryops.quality.dto.response;

import com.factoryops.quality.entity.DefectSeverity;
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
public class DefectResponse {

    private Long id;

    private String defectType;

    private DefectSeverity severity;

    private String description;

    private LocalDateTime reportedDate;

    private Boolean resolved;

    private Long qualityInspectionId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
