package com.may.backend.controller;

import com.may.backend.dto.request.ServiceCreateRequest;
import com.may.backend.dto.request.ServiceUpdateRequest;
import com.may.backend.dto.response.ServiceResponse;
import com.may.backend.service.ServiceService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/services")
@RequiredArgsConstructor
@Slf4j
public class ServiceController {

    private final ServiceService serviceService;

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SERVICE_READ')")
    public ResponseEntity<ServiceResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(serviceService.getById(id));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SERVICE_READ')")
    public ResponseEntity<Page<ServiceResponse>> getAll(
            @RequestParam Map<String, String> filters,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(serviceService.getAll(filters, pageable));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SERVICE_CREATE')")
    public ResponseEntity<ServiceResponse> create(
            @Valid @RequestBody ServiceCreateRequest request,
            HttpServletRequest httpRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(serviceService.create(request, httpRequest.getRemoteAddr()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SERVICE_UPDATE')")
    public ResponseEntity<ServiceResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ServiceUpdateRequest request,
            HttpServletRequest httpRequest) {
        return ResponseEntity.ok(serviceService.update(id, request, httpRequest.getRemoteAddr()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SERVICE_DELETE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        serviceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
