package com.may.backend.dto.request;

import com.may.backend.enums.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestUpdateRequest {

    @NotBlank(message = "Test adı boş olamaz")
    @Size(max = 200, message = "Test adı en fazla 200 karakter olabilir")
    private String name;

    @Size(max = 500, message = "Açıklama en fazla 500 karakter olabilir")
    private String description;

    @NotNull(message = "Modül ID boş olamaz")
    private Long moduleId;

    @Size(max = 1000, message = "Not en fazla 1000 karakter olabilir")
    private String note;

    @NotNull(message = "Durum boş olamaz")
    private Status status;
}
