package com.may.backend.controller;

import com.may.backend.dto.response.UserSessionResponse;
import com.may.backend.entity.UserEntity;
import com.may.backend.repository.UserRepository;
import com.may.backend.service.UserSessionService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/sessions")
@RequiredArgsConstructor
public class SessionController {

    private static final String REFRESH_TOKEN_COOKIE = "refreshToken";

    private final UserSessionService userSessionService;
    private final UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<Page<UserSessionResponse>> getMySessions(
            @AuthenticationPrincipal UserDetails userDetails,
            HttpServletRequest request,
            @RequestParam Map<String, String> filters,
            @PageableDefault(size = 20) Pageable pageable) {

        UserEntity user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();

        String refreshToken = extractRefreshToken(request);
        String currentHash = refreshToken != null ? userSessionService.hashToken(refreshToken) : "";

        Page<UserSessionResponse> sessions = userSessionService.getActiveSessions(
                user.getId(), currentHash, new HashMap<>(filters), pageable);
        return ResponseEntity.ok(sessions);
    }

    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Void> terminateSession(
            @PathVariable Long sessionId,
            @AuthenticationPrincipal UserDetails userDetails,
            HttpServletRequest request) {

        String ip = request.getRemoteAddr();
        userSessionService.revokeSession(sessionId, userDetails.getUsername(), ip);
        return ResponseEntity.ok().build();
    }

    private String extractRefreshToken(HttpServletRequest request) {
        if (request.getCookies() != null) {
            String fromCookie = Arrays.stream(request.getCookies())
                    .filter(c -> REFRESH_TOKEN_COOKIE.equals(c.getName()))
                    .findFirst()
                    .map(Cookie::getValue)
                    .orElse(null);
            if (fromCookie != null) {
                return fromCookie;
            }
        }
        return null;
    }
}
