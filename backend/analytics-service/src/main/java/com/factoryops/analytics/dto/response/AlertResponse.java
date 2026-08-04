package com.factoryops.analytics.dto.response;

import com.factoryops.analytics.entity.AlertSeverity;
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
public class AlertResponse {

    private Long id;

    private String title;

    private String message;

    private AlertSeverity severity;

    private String sourceService;

    private LocalDateTime triggeredAt;

    private Boolean acknowledged;

    private String acknowledgedBy;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
