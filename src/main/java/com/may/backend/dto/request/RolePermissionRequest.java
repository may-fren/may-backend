package com.may.backend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RolePermissionRequest {

    @NotNull(message = "Rol ID boş olamaz")
    private Long roleId;

    @NotNull(message = "İzin ID boş olamaz")
    private Long permissionId;
}
