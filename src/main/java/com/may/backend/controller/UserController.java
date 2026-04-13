package com.may.backend.controller;

import com.may.backend.dto.request.UserCreateRequest;
import com.may.backend.dto.request.UserUpdateRequest;
import com.may.backend.dto.response.UserResponse;
import com.may.backend.enums.Status;
import com.may.backend.service.UserService;
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
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_READ')")
    public ResponseEntity<UserResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('USER_READ')")
    public ResponseEntity<Page<UserResponse>> getAll(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(userService.getAll(pageable));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAuthority('USER_READ')")
    public ResponseEntity<Page<UserResponse>> getAllByStatus(
            @PathVariable Status status,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(userService.getAllByStatus(status, pageable));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('USER_CREATE')")
    public ResponseEntity<UserResponse> create(
            @Valid @RequestBody UserCreateRequest request,
            HttpServletRequest httpRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.create(request, httpRequest.getRemoteAddr()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_UPDATE')")
    public ResponseEntity<UserResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request,
            HttpServletRequest httpRequest) {
        return ResponseEntity.ok(userService.update(id, request, httpRequest.getRemoteAddr()));
    }

    @PatchMapping("/{id}/status/{status}")
    @PreAuthorize("hasAuthority('USER_UPDATE')")
    public ResponseEntity<Void> changeStatus(
            @PathVariable Long id,
            @PathVariable Status status,
            HttpServletRequest httpRequest) {
        userService.changeStatus(id, status, httpRequest.getRemoteAddr());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_DELETE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
