package com.factoryops.inventory.dto.response;

import com.factoryops.inventory.entity.UnitOfMeasure;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaterialResponse {

    private Long id;

    private String code;

    private String name;

    private String description;

    private UnitOfMeasure unitOfMeasure;

    private String category;

    private Integer reorderLevel;

    private Boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
