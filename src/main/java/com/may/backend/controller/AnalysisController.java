package com.may.backend.controller;

import com.may.backend.dto.request.AnalysisCreateRequest;
import com.may.backend.dto.request.AnalysisUpdateRequest;
import com.may.backend.dto.response.AnalysisResponse;
import com.may.backend.service.AnalysisService;
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
@RequestMapping("/api/v1/analyses")
@RequiredArgsConstructor
@Slf4j
public class AnalysisController {

    private final AnalysisService analysisService;

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ANALYSIS_READ')")
    public ResponseEntity<AnalysisResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(analysisService.getById(id));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ANALYSIS_READ')")
    public ResponseEntity<Page<AnalysisResponse>> getAll(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(analysisService.getAll(pageable));
    }

    @GetMapping("/module/{moduleId}")
    @PreAuthorize("hasAuthority('ANALYSIS_READ')")
    public ResponseEntity<List<AnalysisResponse>> getByModuleId(@PathVariable Long moduleId) {
        return ResponseEntity.ok(analysisService.getByModuleId(moduleId));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ANALYSIS_CREATE')")
    public ResponseEntity<AnalysisResponse> create(
            @Valid @RequestBody AnalysisCreateRequest request,
            HttpServletRequest httpRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(analysisService.create(request, httpRequest.getRemoteAddr()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ANALYSIS_UPDATE')")
    public ResponseEntity<AnalysisResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody AnalysisUpdateRequest request,
            HttpServletRequest httpRequest) {
        return ResponseEntity.ok(analysisService.update(id, request, httpRequest.getRemoteAddr()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ANALYSIS_DELETE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        analysisService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
