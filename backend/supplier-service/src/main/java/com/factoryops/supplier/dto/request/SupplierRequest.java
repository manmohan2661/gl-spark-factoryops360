package com.factoryops.supplier.dto.request;

import com.factoryops.supplier.entity.SupplierStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class SupplierRequest {

    @NotBlank
    private String code;

    @NotBlank
    private String name;

    private String contactPerson;

    @Email
    private String email;

    private String phone;

    private String address;

    private String city;

    private String country;

    @NotNull
    private SupplierStatus status;

    private Double rating;
}
