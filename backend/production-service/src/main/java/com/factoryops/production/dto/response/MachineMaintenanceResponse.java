package com.factoryops.production.dto.response;

import com.factoryops.production.entity.MaintenanceStatus;
import com.factoryops.production.entity.MaintenanceType;
import java.time.LocalDate;
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
public class MachineMaintenanceResponse {

    private Long id;

    private MaintenanceType maintenanceType;

    private MaintenanceStatus status;

    private LocalDate scheduledDate;

    private LocalDate completedDate;

    private String remarks;

    private Long machineId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
