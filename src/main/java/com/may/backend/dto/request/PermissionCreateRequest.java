package com.may.backend.dto.request;

import com.may.backend.enums.PermissionAction;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionCreateRequest {

    @NotBlank(message = "İzin adı boş olamaz")
    @Size(max = 100, message = "İzin adı en fazla 100 karakter olabilir")
    private String name;

    @Size(max = 500, message = "Açıklama en fazla 500 karakter olabilir")
    private String description;

    @NotBlank(message = "Modül boş olamaz")
    @Size(max = 100, message = "Modül adı en fazla 100 karakter olabilir")
    private String module;

    @NotNull(message = "Aksiyon boş olamaz")
    private PermissionAction action;
}
