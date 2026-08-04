package com.factoryops.production.mapper;

import com.factoryops.production.dto.request.ProductionBatchRequest;
import com.factoryops.production.dto.response.ProductionBatchResponse;
import com.factoryops.production.entity.ProductionBatch;

import java.util.List;

// Plain mapper contract. MapStruct is not yet a declared dependency of this
// module's pom.xml; once added, this interface can be annotated with
// @Mapper(componentModel = "spring") and left otherwise unchanged.
public interface ProductionBatchMapper {

    ProductionBatch toEntity(ProductionBatchRequest request);

    ProductionBatchResponse toResponse(ProductionBatch entity);

    List<ProductionBatchResponse> toResponseList(List<ProductionBatch> entities);
}
