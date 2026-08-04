package com.factoryops.auth.service;

import com.factoryops.auth.dto.request.RoleRequest;
import com.factoryops.auth.dto.response.RoleResponse;

import java.util.List;

public interface RoleService {

    RoleResponse create(RoleRequest request);

    RoleResponse getById(Long id);

    List<RoleResponse> getAll();

    RoleResponse update(Long id, RoleRequest request);

    void delete(Long id);
}
