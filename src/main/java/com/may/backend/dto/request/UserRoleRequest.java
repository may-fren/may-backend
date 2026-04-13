package com.may.backend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleRequest {

    @NotNull(message = "Kullanıcı ID boş olamaz")
    private Long userId;

    @NotNull(message = "Rol ID boş olamaz")
    private Long roleId;
}
