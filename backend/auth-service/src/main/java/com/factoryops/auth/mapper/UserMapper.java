package com.factoryops.auth.mapper;

import com.factoryops.auth.dto.request.UserRequest;
import com.factoryops.auth.dto.response.UserResponse;
import com.factoryops.auth.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    public User toEntity(UserRequest request) {

        if (request == null) {
            return null;
        }

        return User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())
                .fullName(request.getFullName())
                .phoneNumber(request.getPhoneNumber())
                .active(request.getActive())
                .build();
    }

    public UserResponse toResponse(User entity) {

        if (entity == null) {
            return null;
        }

        return UserResponse.builder()
                .userId(entity.getUserId())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .fullName(entity.getFullName())
                .phoneNumber(entity.getPhoneNumber())
                .active(entity.getActive())
                .roleId(entity.getRole() != null ? entity.getRole().getRoleId() : null)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public List<UserResponse> toResponseList(List<User> entities) {

        return entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}