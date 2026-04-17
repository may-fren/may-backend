package com.may.backend.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    ENTITY_NOT_FOUND(1001, "Kayıt bulunamadı"),
    DUPLICATE_ENTRY(1002, "Bu kayıt zaten mevcut"),
    VALIDATION_ERROR(1003, "Validasyon hatası"),
    BUSINESS_ERROR(1004, "İş kuralı hatası"),
    INTERNAL_ERROR(1005, "Beklenmeyen bir hata oluştu"),
    ACCESS_DENIED(1006, "Erişim reddedildi"),
    SESSION_LIMIT_REACHED(1007, "Oturum limiti aşıldı"),
    SESSION_NOT_FOUND(1008, "Oturum bulunamadı"),
    INVALID_REFRESH_TOKEN(1009, "Geçersiz yenileme tokeni"),
    INVALID_PLATFORM(1010, "Geçersiz platform bilgisi"),
    AUTHENTICATION_FAILED(1011, "Kimlik doğrulama hatası");

    private final int code;
    private final String message;
}
