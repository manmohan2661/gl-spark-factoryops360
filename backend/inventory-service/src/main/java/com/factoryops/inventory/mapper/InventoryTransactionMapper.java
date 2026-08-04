package com.factoryops.inventory.mapper;

import com.factoryops.inventory.dto.request.InventoryTransactionRequest;
import com.factoryops.inventory.dto.response.InventoryTransactionResponse;
import com.factoryops.inventory.entity.InventoryTransaction;

import java.util.List;

// Plain mapper contract. MapStruct is not yet a declared dependency of this
// module's pom.xml; once added, this interface can be annotated with
// @Mapper(componentModel = "spring") and left otherwise unchanged.
public interface InventoryTransactionMapper {

    InventoryTransaction toEntity(InventoryTransactionRequest request);

    InventoryTransactionResponse toResponse(InventoryTransaction entity);

    List<InventoryTransactionResponse> toResponseList(List<InventoryTransaction> entities);
}
