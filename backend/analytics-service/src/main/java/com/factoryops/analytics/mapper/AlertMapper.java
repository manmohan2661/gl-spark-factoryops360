package com.factoryops.analytics.mapper;

import com.factoryops.analytics.dto.request.AlertRequest;
import com.factoryops.analytics.dto.response.AlertResponse;
import com.factoryops.analytics.entity.Alert;

import java.util.List;

// Plain mapper contract. MapStruct is not yet a declared dependency of this
// module's pom.xml; once added, this interface can be annotated with
// @Mapper(componentModel = "spring") and left otherwise unchanged.
public interface AlertMapper {

    Alert toEntity(AlertRequest request);

    AlertResponse toResponse(Alert entity);

    List<AlertResponse> toResponseList(List<Alert> entities);
}
