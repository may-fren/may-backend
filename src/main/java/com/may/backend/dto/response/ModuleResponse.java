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
public class ModuleResponse {

    private Long id;
    private String name;
    private String brand;
    private String model;
    private Integer year;
    private String code;
    private Status status;
    @JsonFormat(pattern = "yyyyMMddHHmmss")
    private LocalDateTime createdDate;
    private String createdBy;
}
