package com.factoryops.quality.mapper;

import com.factoryops.quality.dto.request.DefectRequest;
import com.factoryops.quality.dto.response.DefectResponse;
import com.factoryops.quality.entity.Defect;

import java.util.List;

// Plain mapper contract. MapStruct is not yet a declared dependency of this
// module's pom.xml; once added, this interface can be annotated with
// @Mapper(componentModel = "spring") and left otherwise unchanged.
public interface DefectMapper {

    Defect toEntity(DefectRequest request);

    DefectResponse toResponse(Defect entity);

    List<DefectResponse> toResponseList(List<Defect> entities);
}
