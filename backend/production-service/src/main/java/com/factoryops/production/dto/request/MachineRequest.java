package com.factoryops.production.dto.request;

import com.factoryops.production.entity.MachineStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
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
public class MachineRequest {

    @NotBlank
    private String machineCode;

    @NotBlank
    private String name;

    private String type;

    @NotNull
    private MachineStatus status;

    private String location;

    private LocalDate installationDate;
}
