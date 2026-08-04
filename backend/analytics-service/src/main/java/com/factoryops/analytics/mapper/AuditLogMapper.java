package com.factoryops.analytics.mapper;

import com.factoryops.analytics.dto.request.AuditLogRequest;
import com.factoryops.analytics.dto.response.AuditLogResponse;
import com.factoryops.analytics.entity.AuditLog;

import java.util.List;

// Plain mapper contract. MapStruct is not yet a declared dependency of this
// module's pom.xml; once added, this interface can be annotated with
// @Mapper(componentModel = "spring") and left otherwise unchanged.
public interface AuditLogMapper {

    AuditLog toEntity(AuditLogRequest request);

    AuditLogResponse toResponse(AuditLog entity);

    List<AuditLogResponse> toResponseList(List<AuditLog> entities);
}
