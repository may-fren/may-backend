package com.may.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.may.backend.enums.Status;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleResponse {

    private Long id;
    private String name;
    private String description;
    private Status status;
    @JsonFormat(pattern = "yyyyMMddHHmmss")
    private LocalDateTime createdDate;
    private String createdBy;
}
