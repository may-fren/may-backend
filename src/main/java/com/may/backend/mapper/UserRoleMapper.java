package com.may.backend.mapper;

import com.may.backend.dto.response.UserRoleResponse;
import com.may.backend.entity.UserRoleEntity;
import org.springframework.stereotype.Component;

@Component
public class UserRoleMapper {

    public UserRoleResponse toResponse(UserRoleEntity entity) {
        return UserRoleResponse.builder()
                .id(entity.getId())
                .userId(entity.getUser().getId())
                .username(entity.getUser().getUsername())
                .roleId(entity.getRole().getId())
                .roleName(entity.getRole().getName())
                .assignedBy(entity.getAssignedBy())
                .assignedDate(entity.getAssignedDate())
                .build();
    }
}
