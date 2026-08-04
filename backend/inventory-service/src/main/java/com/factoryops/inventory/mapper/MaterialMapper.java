package com.factoryops.inventory.mapper;

import com.factoryops.inventory.dto.request.MaterialRequest;
import com.factoryops.inventory.dto.response.MaterialResponse;
import com.factoryops.inventory.entity.Material;

import java.util.List;

// Plain mapper contract. MapStruct is not yet a declared dependency of this
// module's pom.xml; once added, this interface can be annotated with
// @Mapper(componentModel = "spring") and left otherwise unchanged.
public interface MaterialMapper {

    Material toEntity(MaterialRequest request);

    MaterialResponse toResponse(Material entity);

    List<MaterialResponse> toResponseList(List<Material> entities);
}
