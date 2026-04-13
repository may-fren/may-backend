package com.may.backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateRequest {

    @NotBlank(message = "Kullanıcı adı boş olamaz")
    @Size(max = 100, message = "Kullanıcı adı en fazla 100 karakter olabilir")
    private String username;

    @NotBlank(message = "Email boş olamaz")
    @Email(message = "Geçerli bir email adresi giriniz")
    @Size(max = 150, message = "Email en fazla 150 karakter olabilir")
    private String email;

    @NotBlank(message = "Şifre boş olamaz")
    @Size(min = 8, max = 255, message = "Şifre en az 8 karakter olmalıdır")
    private String password;

    @NotBlank(message = "Ad boş olamaz")
    @Size(max = 100, message = "Ad en fazla 100 karakter olabilir")
    private String firstName;

    @NotBlank(message = "Soyad boş olamaz")
    @Size(max = 100, message = "Soyad en fazla 100 karakter olabilir")
    private String lastName;

    @Size(max = 20, message = "Telefon en fazla 20 karakter olabilir")
    private String phone;
}
