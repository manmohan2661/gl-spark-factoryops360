package com.factoryops.production.dto.response;

import java.time.LocalDateTime;
import java.time.LocalTime;
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
public class ShiftResponse {

    private Long id;

    private String shiftName;

    private LocalTime startTime;

    private LocalTime endTime;

    private String supervisorName;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
