package com.may.backend.controller;

import com.may.backend.dto.request.RolePermissionRequest;
import com.may.backend.dto.response.RolePermissionResponse;
import com.may.backend.service.RolePermissionService;
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
@RequestMapping("/api/v1/role-permissions")
@RequiredArgsConstructor
@Slf4j
public class RolePermissionController {

    private final RolePermissionService rolePermissionService;

    @GetMapping("/role/{roleId}")
    @PreAuthorize("hasAuthority('ROLES_READ')")
    public ResponseEntity<Page<RolePermissionResponse>> getByRoleId(
            @PathVariable Long roleId,
            @RequestParam Map<String, String> filters,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(rolePermissionService.getByRoleId(roleId, filters, pageable));
    }

    @GetMapping("/permission/{permissionId}")
    @PreAuthorize("hasAuthority('PERMISSION_READ')")
    public ResponseEntity<Page<RolePermissionResponse>> getByPermissionId(
            @PathVariable Long permissionId,
            @RequestParam Map<String, String> filters,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(rolePermissionService.getByPermissionId(permissionId, filters, pageable));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLES_UPDATE')")
    public ResponseEntity<RolePermissionResponse> assign(
            @Valid @RequestBody RolePermissionRequest request,
            HttpServletRequest httpRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(rolePermissionService.assign(request, httpRequest.getRemoteAddr()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLES_UPDATE')")
    public ResponseEntity<Void> remove(@PathVariable Long id) {
        rolePermissionService.remove(id);
        return ResponseEntity.noContent().build();
    }
}
