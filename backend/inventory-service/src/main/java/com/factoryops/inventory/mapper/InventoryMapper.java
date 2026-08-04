package com.factoryops.inventory.mapper;

import com.factoryops.inventory.dto.request.InventoryRequest;
import com.factoryops.inventory.dto.response.InventoryResponse;
import com.factoryops.inventory.entity.Inventory;

import java.util.List;

// Plain mapper contract. MapStruct is not yet a declared dependency of this
// module's pom.xml; once added, this interface can be annotated with
// @Mapper(componentModel = "spring") and left otherwise unchanged.
public interface InventoryMapper {

    Inventory toEntity(InventoryRequest request);

    InventoryResponse toResponse(Inventory entity);

    List<InventoryResponse> toResponseList(List<Inventory> entities);
}
