package com.may.backend.mapper;

import com.may.backend.dto.request.UserCreateRequest;
import com.may.backend.dto.response.UserResponse;
import com.may.backend.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(UserEntity entity) {
        return UserResponse.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .phone(entity.getPhone())
                .status(entity.getStatus())
                .lastLoginDate(entity.getLastLoginDate())
                .errorLoginCount(entity.getErrorLoginCount())
                .createdDate(entity.getCreatedDate())
                .createdBy(entity.getCreatedBy())
                .build();
    }

    public UserEntity toEntity(UserCreateRequest request, String passwordHash) {
        UserEntity entity = new UserEntity();
        entity.setUsername(request.getUsername());
        entity.setEmail(request.getEmail());
        entity.setPasswordHash(passwordHash);
        entity.setFirstName(request.getFirstName());
        entity.setLastName(request.getLastName());
        entity.setPhone(request.getPhone());
        return entity;
    }
}
