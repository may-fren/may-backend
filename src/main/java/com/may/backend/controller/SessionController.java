package com.may.backend.controller;

import com.may.backend.dto.response.UserSessionResponse;
import com.may.backend.entity.UserEntity;
import com.may.backend.repository.UserRepository;
import com.may.backend.service.UserSessionService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/sessions")
@RequiredArgsConstructor
public class SessionController {

    private static final String REFRESH_TOKEN_COOKIE = "refreshToken";

    private final UserSessionService userSessionService;
    private final UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<List<UserSessionResponse>> getMySessions(
            @AuthenticationPrincipal UserDetails userDetails,
            HttpServletRequest request,
            @RequestBody(required = false) Map<String, String> body) {

        UserEntity user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();

        String refreshToken = extractRefreshToken(request, body);
        String currentHash = refreshToken != null ? userSessionService.hashToken(refreshToken) : "";

        List<UserSessionResponse> sessions = userSessionService.getActiveSessions(user.getId(), currentHash);
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

    private String extractRefreshToken(HttpServletRequest request, Map<String, String> body) {
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
        if (body != null && body.containsKey("refreshToken")) {
            return body.get("refreshToken");
        }
        return null;
    }
}
