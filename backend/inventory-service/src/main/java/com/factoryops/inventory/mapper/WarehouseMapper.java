package com.factoryops.inventory.mapper;

import com.factoryops.inventory.dto.request.WarehouseRequest;
import com.factoryops.inventory.dto.response.WarehouseResponse;
import com.factoryops.inventory.entity.Warehouse;

import java.util.List;

// Plain mapper contract. MapStruct is not yet a declared dependency of this
// module's pom.xml; once added, this interface can be annotated with
// @Mapper(componentModel = "spring") and left otherwise unchanged.
public interface WarehouseMapper {

    Warehouse toEntity(WarehouseRequest request);

    WarehouseResponse toResponse(Warehouse entity);

    List<WarehouseResponse> toResponseList(List<Warehouse> entities);
}
