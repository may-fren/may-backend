package com.may.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleResponse {

    private Long id;
    private Long userId;
    private String username;
    private Long roleId;
    private String roleName;
    private String assignedBy;
    @JsonFormat(pattern = "yyyyMMddHHmmss")
    private LocalDateTime assignedDate;
}
