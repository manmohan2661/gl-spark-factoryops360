package com.factoryops.production.mapper;

import com.factoryops.production.dto.request.ShiftRequest;
import com.factoryops.production.dto.response.ShiftResponse;
import com.factoryops.production.entity.Shift;

import java.util.List;

// Plain mapper contract. MapStruct is not yet a declared dependency of this
// module's pom.xml; once added, this interface can be annotated with
// @Mapper(componentModel = "spring") and left otherwise unchanged.
public interface ShiftMapper {

    Shift toEntity(ShiftRequest request);

    ShiftResponse toResponse(Shift entity);

    List<ShiftResponse> toResponseList(List<Shift> entities);
}
