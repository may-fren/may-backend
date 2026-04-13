package com.may.backend.controller;

import com.may.backend.dto.request.RolePermissionRequest;
import com.may.backend.dto.response.RolePermissionResponse;
import com.may.backend.service.RolePermissionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/role-permissions")
@RequiredArgsConstructor
@Slf4j
public class RolePermissionController {

    private final RolePermissionService rolePermissionService;

    @GetMapping("/role/{roleId}")
    @PreAuthorize("hasAuthority('ROLES_READ')")
    public ResponseEntity<List<RolePermissionResponse>> getByRoleId(@PathVariable Long roleId) {
        return ResponseEntity.ok(rolePermissionService.getByRoleId(roleId));
    }

    @GetMapping("/permission/{permissionId}")
    @PreAuthorize("hasAuthority('PERMISSION_READ')")
    public ResponseEntity<List<RolePermissionResponse>> getByPermissionId(@PathVariable Long permissionId) {
        return ResponseEntity.ok(rolePermissionService.getByPermissionId(permissionId));
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
