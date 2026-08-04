package com.factoryops.production.mapper;

import com.factoryops.production.dto.request.ProductionOrderRequest;
import com.factoryops.production.dto.response.ProductionOrderResponse;
import com.factoryops.production.entity.ProductionOrder;

import java.util.List;

// Plain mapper contract. MapStruct is not yet a declared dependency of this
// module's pom.xml; once added, this interface can be annotated with
// @Mapper(componentModel = "spring") and left otherwise unchanged.
public interface ProductionOrderMapper {

    ProductionOrder toEntity(ProductionOrderRequest request);

    ProductionOrderResponse toResponse(ProductionOrder entity);

    List<ProductionOrderResponse> toResponseList(List<ProductionOrder> entities);
}
