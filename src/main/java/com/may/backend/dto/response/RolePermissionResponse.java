package com.may.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.may.backend.enums.PermissionAction;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RolePermissionResponse {

    private Long id;
    private Long roleId;
    private String roleName;
    private Long permissionId;
    private String permissionName;
    private String module;
    private PermissionAction action;
    @JsonFormat(pattern = "yyyyMMddHHmmss")
    private LocalDateTime createdDate;
}
