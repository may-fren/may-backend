package com.may.backend.mapper;

import com.may.backend.dto.request.PermissionCreateRequest;
import com.may.backend.dto.response.PermissionResponse;
import com.may.backend.entity.PermissionEntity;
import org.springframework.stereotype.Component;

@Component
public class PermissionMapper {

    public PermissionResponse toResponse(PermissionEntity entity) {
        return PermissionResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .module(entity.getModule())
                .action(entity.getAction())
                .status(entity.getStatus())
                .createdDate(entity.getCreatedDate())
                .createdBy(entity.getCreatedBy())
                .build();
    }

    public PermissionEntity toEntity(PermissionCreateRequest request) {
        PermissionEntity entity = new PermissionEntity();
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setModule(request.getModule());
        entity.setAction(request.getAction());
        return entity;
    }
}
