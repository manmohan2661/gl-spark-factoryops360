package com.factoryops.supplier.mapper;

import com.factoryops.supplier.dto.request.SupplierPerformanceRequest;
import com.factoryops.supplier.dto.response.SupplierPerformanceResponse;
import com.factoryops.supplier.entity.SupplierPerformance;

import java.util.List;

// Plain mapper contract. MapStruct is not yet a declared dependency of this
// module's pom.xml; once added, this interface can be annotated with
// @Mapper(componentModel = "spring") and left otherwise unchanged.
public interface SupplierPerformanceMapper {

    SupplierPerformance toEntity(SupplierPerformanceRequest request);

    SupplierPerformanceResponse toResponse(SupplierPerformance entity);

    List<SupplierPerformanceResponse> toResponseList(List<SupplierPerformance> entities);
}
