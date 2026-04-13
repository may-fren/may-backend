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
    ACCESS_DENIED(1006, "Erişim reddedildi");

    private final int code;
    private final String message;
}
