package com.factoryops.production.mapper;

import com.factoryops.production.dto.request.MachineRequest;
import com.factoryops.production.dto.response.MachineResponse;
import com.factoryops.production.entity.Machine;

import java.util.List;

// Plain mapper contract. MapStruct is not yet a declared dependency of this
// module's pom.xml; once added, this interface can be annotated with
// @Mapper(componentModel = "spring") and left otherwise unchanged.
public interface MachineMapper {

    Machine toEntity(MachineRequest request);

    MachineResponse toResponse(Machine entity);

    List<MachineResponse> toResponseList(List<Machine> entities);
}
