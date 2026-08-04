package com.factoryops.auth.service.impl;

import com.factoryops.auth.dto.request.RoleRequest;
import com.factoryops.auth.dto.response.RoleResponse;
import com.factoryops.auth.entity.Role;
import com.factoryops.auth.exception.DuplicateResourceException;
import com.factoryops.auth.exception.ResourceNotFoundException;
import com.factoryops.auth.mapper.RoleMapper;
import com.factoryops.auth.repository.RoleRepository;
import com.factoryops.auth.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    @Override
    public RoleResponse create(RoleRequest request) {

        if (roleRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException(
                    "Role " + request.getName() + " already exists");
        }

        Role role = roleMapper.toEntity(request);

        role.setCreatedAt(LocalDateTime.now());
        role.setUpdatedAt(LocalDateTime.now());

        Role savedRole = roleRepository.save(role);

        return roleMapper.toResponse(savedRole);
    }

    @Override
    public RoleResponse getById(Long id) {

        Role role = roleRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Role not found with id : " + id));

        return roleMapper.toResponse(role);
    }

    @Override
    public List<RoleResponse> getAll() {

        List<Role> roles = roleRepository.findAll();

        return roleMapper.toResponseList(roles);
    }

    @Override
    public RoleResponse update(Long id, RoleRequest request) {

        Role role = roleRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Role not found with id : " + id));

        if (!role.getName().equals(request.getName())
                && roleRepository.existsByName(request.getName())) {

            throw new DuplicateResourceException(
                    "Role " + request.getName() + " already exists");
        }

        role.setName(request.getName());
        role.setDescription(request.getDescription());
        role.setUpdatedAt(LocalDateTime.now());

        Role updatedRole = roleRepository.save(role);

        return roleMapper.toResponse(updatedRole);
    }

    @Override
    public void delete(Long id) {

        Role role = roleRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Role not found with id : " + id));

        roleRepository.delete(role);
    }
}