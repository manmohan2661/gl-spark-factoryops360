package com.factoryops.supplier.mapper;

import com.factoryops.supplier.dto.request.SupplierRequest;
import com.factoryops.supplier.dto.response.SupplierResponse;
import com.factoryops.supplier.entity.Supplier;

import java.util.List;

// Plain mapper contract. MapStruct is not yet a declared dependency of this
// module's pom.xml; once added, this interface can be annotated with
// @Mapper(componentModel = "spring") and left otherwise unchanged.
public interface SupplierMapper {

    Supplier toEntity(SupplierRequest request);

    SupplierResponse toResponse(Supplier entity);

    List<SupplierResponse> toResponseList(List<Supplier> entities);
}
