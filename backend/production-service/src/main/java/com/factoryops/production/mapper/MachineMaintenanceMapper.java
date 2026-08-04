package com.factoryops.production.mapper;

import com.factoryops.production.dto.request.MachineMaintenanceRequest;
import com.factoryops.production.dto.response.MachineMaintenanceResponse;
import com.factoryops.production.entity.MachineMaintenance;

import java.util.List;

// Plain mapper contract. MapStruct is not yet a declared dependency of this
// module's pom.xml; once added, this interface can be annotated with
// @Mapper(componentModel = "spring") and left otherwise unchanged.
public interface MachineMaintenanceMapper {

    MachineMaintenance toEntity(MachineMaintenanceRequest request);

    MachineMaintenanceResponse toResponse(MachineMaintenance entity);

    List<MachineMaintenanceResponse> toResponseList(List<MachineMaintenance> entities);
}
