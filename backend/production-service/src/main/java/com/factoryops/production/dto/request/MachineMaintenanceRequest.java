package com.factoryops.production.dto.request;

import com.factoryops.production.entity.MaintenanceStatus;
import com.factoryops.production.entity.MaintenanceType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MachineMaintenanceRequest {

    @NotNull(message = "Maintenance type is required")
    private MaintenanceType maintenanceType;

    @NotNull(message = "Maintenance status is required")
    private MaintenanceStatus status;

    private LocalDate scheduledDate;

    private LocalDate completedDate;

    @Size(max = 500, message = "Remarks cannot exceed 500 characters")
    private String remarks;

    @NotNull(message = "Machine id is required")
    private Long machineId;
}