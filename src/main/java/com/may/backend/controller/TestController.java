package com.may.backend.controller;

import com.may.backend.dto.request.TestCreateRequest;
import com.may.backend.dto.request.TestUpdateRequest;
import com.may.backend.dto.response.TestResponse;
import com.may.backend.service.TestService;
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

import java.util.List;

@RestController
@RequestMapping("/api/v1/tests")
@RequiredArgsConstructor
@Slf4j
public class TestController {

    private final TestService testService;

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('TEST_READ')")
    public ResponseEntity<TestResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(testService.getById(id));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('TEST_READ')")
    public ResponseEntity<Page<TestResponse>> getAll(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(testService.getAll(pageable));
    }

    @GetMapping("/module/{moduleId}")
    @PreAuthorize("hasAuthority('TEST_READ')")
    public ResponseEntity<List<TestResponse>> getByModuleId(@PathVariable Long moduleId) {
        return ResponseEntity.ok(testService.getByModuleId(moduleId));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('TEST_CREATE')")
    public ResponseEntity<TestResponse> create(
            @Valid @RequestBody TestCreateRequest request,
            HttpServletRequest httpRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(testService.create(request, httpRequest.getRemoteAddr()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('TEST_UPDATE')")
    public ResponseEntity<TestResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody TestUpdateRequest request,
            HttpServletRequest httpRequest) {
        return ResponseEntity.ok(testService.update(id, request, httpRequest.getRemoteAddr()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('TEST_DELETE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        testService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
