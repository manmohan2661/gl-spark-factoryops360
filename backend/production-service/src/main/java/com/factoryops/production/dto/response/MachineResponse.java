package com.factoryops.production.dto.response;

import com.factoryops.production.entity.MachineStatus;
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
public class MachineResponse {

    private Long id;

    private String machineCode;

    private String name;

    private String type;

    private MachineStatus status;

    private String location;

    private LocalDate installationDate;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
