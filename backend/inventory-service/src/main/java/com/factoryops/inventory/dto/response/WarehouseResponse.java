package com.factoryops.inventory.dto.response;

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
public class WarehouseResponse {

    private Long id;

    private String code;

    private String name;

    private String location;

    private Integer capacity;

    private Boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
