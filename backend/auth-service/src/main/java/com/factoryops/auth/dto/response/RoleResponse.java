package com.factoryops.auth.dto.response;

import com.factoryops.auth.entity.RoleName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleResponse {

    private Long roleId;

    private RoleName name;

    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}