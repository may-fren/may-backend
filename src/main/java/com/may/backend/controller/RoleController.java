package com.may.backend.controller;

import com.may.backend.dto.request.RoleCreateRequest;
import com.may.backend.dto.request.RoleUpdateRequest;
import com.may.backend.dto.response.RoleResponse;
import com.may.backend.service.RoleService;
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
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@Slf4j
public class RoleController {

    private final RoleService roleService;

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLES_READ')")
    public ResponseEntity<RoleResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(roleService.getById(id));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLES_READ')")
    public ResponseEntity<Page<RoleResponse>> getAll(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(roleService.getAll(pageable));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLES_CREATE')")
    public ResponseEntity<RoleResponse> create(
            @Valid @RequestBody RoleCreateRequest request,
            HttpServletRequest httpRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(roleService.create(request, httpRequest.getRemoteAddr()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLES_UPDATE')")
    public ResponseEntity<RoleResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody RoleUpdateRequest request,
            HttpServletRequest httpRequest) {
        return ResponseEntity.ok(roleService.update(id, request, httpRequest.getRemoteAddr()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLES_DELETE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        roleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
