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
public class ModuleUpdateRequest {

    @NotBlank(message = "Modül adı boş olamaz")
    @Size(max = 200, message = "Modül adı en fazla 200 karakter olabilir")
    private String name;

    @NotBlank(message = "Marka boş olamaz")
    @Size(max = 100, message = "Marka en fazla 100 karakter olabilir")
    private String brand;

    @NotBlank(message = "Model boş olamaz")
    @Size(max = 100, message = "Model en fazla 100 karakter olabilir")
    private String model;

    private Integer year;

    @NotBlank(message = "Kod boş olamaz")
    @Size(max = 100, message = "Kod en fazla 100 karakter olabilir")
    private String code;

    @NotNull(message = "Durum boş olamaz")
    private Status status;
}
