package com.may.backend.mapper;

import com.may.backend.dto.request.RoleCreateRequest;
import com.may.backend.dto.response.RoleResponse;
import com.may.backend.entity.RoleEntity;
import org.springframework.stereotype.Component;

@Component
public class RoleMapper {

    public RoleResponse toResponse(RoleEntity entity) {
        return RoleResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .status(entity.getStatus())
                .createdDate(entity.getCreatedDate())
                .createdBy(entity.getCreatedBy())
                .build();
    }

    public RoleEntity toEntity(RoleCreateRequest request) {
        RoleEntity entity = new RoleEntity();
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        return entity;
    }
}
