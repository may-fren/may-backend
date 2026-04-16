package com.may.backend.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(SessionLimitException.class)
    public ProblemDetail handleSessionLimitException(SessionLimitException ex, HttpServletRequest request) {
        log.warn("Session limit reached: {}", ex.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(ex.getHttpStatus(), ex.getMessage());
        problemDetail.setTitle(ex.getErrorCode().getMessage());
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        problemDetail.setProperty("code", ex.getErrorCode().getCode());
        problemDetail.setProperty("activeSessions", ex.getActiveSessions());
        return problemDetail;
    }

    @ExceptionHandler(BusinessException.class)
    public ProblemDetail handleBusinessException(BusinessException ex, HttpServletRequest request) {
        log.warn("Business exception: code={}, message={}", ex.getErrorCode().getCode(), ex.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(ex.getHttpStatus(), ex.getMessage());
        problemDetail.setTitle(ex.getErrorCode().getMessage());
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        problemDetail.setProperty("code", ex.getErrorCode().getCode());
        return problemDetail;
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        log.warn("Validation exception: {}", errors);
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validasyon hatası");
        problemDetail.setTitle("Validasyon Hatası");
        problemDetail.setProperty("code", ErrorCode.VALIDATION_ERROR.getCode());
        problemDetail.setProperty("errors", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
        log.warn("Access denied: uri={}", request.getRequestURI());
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "Bu işlem için yetkiniz bulunmamaktadır");
        problemDetail.setTitle("Erişim Reddedildi");
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        problemDetail.setProperty("code", ErrorCode.ACCESS_DENIED.getCode());
        return problemDetail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleException(Exception ex, HttpServletRequest request) {
        log.error("Unexpected exception: ", ex);
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR, "Beklenmeyen bir hata oluştu");
        problemDetail.setTitle("Sunucu Hatası");
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        problemDetail.setProperty("code", ErrorCode.INTERNAL_ERROR.getCode());
        return problemDetail;
    }
}
