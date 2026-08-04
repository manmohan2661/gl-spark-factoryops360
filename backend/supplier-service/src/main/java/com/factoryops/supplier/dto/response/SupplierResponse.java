package com.factoryops.supplier.dto.response;

import com.factoryops.supplier.entity.SupplierStatus;
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
public class SupplierResponse {

    private Long id;

    private String code;

    private String name;

    private String contactPerson;

    private String email;

    private String phone;

    private String address;

    private String city;

    private String country;

    private SupplierStatus status;

    private Double rating;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
