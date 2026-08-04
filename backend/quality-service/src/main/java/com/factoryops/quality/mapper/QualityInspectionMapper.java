package com.factoryops.quality.mapper;

import com.factoryops.quality.dto.request.QualityInspectionRequest;
import com.factoryops.quality.dto.response.QualityInspectionResponse;
import com.factoryops.quality.entity.QualityInspection;

import java.util.List;

// Plain mapper contract. MapStruct is not yet a declared dependency of this
// module's pom.xml; once added, this interface can be annotated with
// @Mapper(componentModel = "spring") and left otherwise unchanged.
public interface QualityInspectionMapper {

    QualityInspection toEntity(QualityInspectionRequest request);

    QualityInspectionResponse toResponse(QualityInspection entity);

    List<QualityInspectionResponse> toResponseList(List<QualityInspection> entities);
}
