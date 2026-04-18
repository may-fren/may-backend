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
public class ServiceUpdateRequest {

    @NotBlank(message = "Servis adı boş olamaz")
    @Size(max = 200, message = "Servis adı en fazla 200 karakter olabilir")
    private String name;

    @Size(max = 100, message = "Şehir en fazla 100 karakter olabilir")
    private String city;

    @Size(max = 100, message = "İlçe en fazla 100 karakter olabilir")
    private String town;

    @Size(max = 500, message = "Adres en fazla 500 karakter olabilir")
    private String address;

    @NotNull(message = "Durum boş olamaz")
    private Status status;
}
