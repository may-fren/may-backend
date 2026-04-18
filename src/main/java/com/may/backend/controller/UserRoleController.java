package com.may.backend.controller;

import com.may.backend.dto.request.UserRoleRequest;
import com.may.backend.dto.response.UserRoleResponse;
import com.may.backend.service.UserRoleService;
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
@RequestMapping("/api/v1/user-roles")
@RequiredArgsConstructor
@Slf4j
public class UserRoleController {

    private final UserRoleService userRoleService;

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('USER_READ')")
    public ResponseEntity<Page<UserRoleResponse>> getByUserId(
            @PathVariable Long userId,
            @RequestParam Map<String, String> filters,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(userRoleService.getByUserId(userId, filters, pageable));
    }

    @GetMapping("/role/{roleId}")
    @PreAuthorize("hasAuthority('ROLES_READ')")
    public ResponseEntity<Page<UserRoleResponse>> getByRoleId(
            @PathVariable Long roleId,
            @RequestParam Map<String, String> filters,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(userRoleService.getByRoleId(roleId, filters, pageable));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('USER_UPDATE')")
    public ResponseEntity<UserRoleResponse> assign(
            @Valid @RequestBody UserRoleRequest request,
            HttpServletRequest httpRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userRoleService.assign(request, httpRequest.getRemoteAddr()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_UPDATE')")
    public ResponseEntity<Void> remove(@PathVariable Long id) {
        userRoleService.remove(id);
        return ResponseEntity.noContent().build();
    }
}
