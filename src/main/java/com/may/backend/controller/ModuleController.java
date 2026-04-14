package com.may.backend.controller;

import com.may.backend.dto.request.ModuleCreateRequest;
import com.may.backend.dto.request.ModuleUpdateRequest;
import com.may.backend.dto.response.ModuleResponse;
import com.may.backend.service.ModuleService;
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

@RestController
@RequestMapping("/api/v1/modules")
@RequiredArgsConstructor
@Slf4j
public class ModuleController {

    private final ModuleService moduleService;

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('MODULE_READ')")
    public ResponseEntity<ModuleResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(moduleService.getById(id));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('MODULE_READ')")
    public ResponseEntity<Page<ModuleResponse>> getAll(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(moduleService.getAll(pageable));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('MODULE_CREATE')")
    public ResponseEntity<ModuleResponse> create(
            @Valid @RequestBody ModuleCreateRequest request,
            HttpServletRequest httpRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(moduleService.create(request, httpRequest.getRemoteAddr()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('MODULE_UPDATE')")
    public ResponseEntity<ModuleResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ModuleUpdateRequest request,
            HttpServletRequest httpRequest) {
        return ResponseEntity.ok(moduleService.update(id, request, httpRequest.getRemoteAddr()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('MODULE_DELETE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        moduleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
