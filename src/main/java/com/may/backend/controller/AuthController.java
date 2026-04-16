package com.may.backend.controller;

import com.may.backend.config.JwtProperties;
import com.may.backend.dto.request.LoginRequest;
import com.may.backend.dto.response.LoginResponse;
import com.may.backend.entity.UserEntity;
import com.may.backend.entity.UserSessionEntity;
import com.may.backend.enums.PlatformType;
import com.may.backend.exception.BusinessException;
import com.may.backend.exception.ErrorCode;
import com.may.backend.repository.UserRepository;
import com.may.backend.security.JwtTokenProvider;
import com.may.backend.service.UserSessionService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final String REFRESH_TOKEN_COOKIE = "refreshToken";
    private static final String REFRESH_TOKEN_PATH = "/api/v1/auth";

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;
    private final UserRepository userRepository;
    private final UserDetailsService userDetailsService;
    private final UserSessionService userSessionService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request,
                                               @RequestHeader(value = "X-Platform", defaultValue = "WEB") String platformHeader,
                                               HttpServletRequest httpRequest) {
        PlatformType platform = parsePlatform(platformHeader);
        String ip = httpRequest.getRemoteAddr();
        String deviceInfo = httpRequest.getHeader("User-Agent");

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        UserEntity user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, HttpStatus.NOT_FOUND));

        if (Boolean.TRUE.equals(request.getForceLogin())) {
            String accessToken = jwtTokenProvider.generateAccessToken(authentication, platform);
            String refreshToken = jwtTokenProvider.generateRefreshToken(authentication, platform);
            userSessionService.createSessionWithForce(user, refreshToken, platform, deviceInfo, ip);

            user.setLastLoginDate(LocalDateTime.now());
            user.setErrorLoginCount(0);
            userRepository.save(user);

            long refreshExpMs = jwtProperties.getRefreshTokenExpiration(platform);
            ResponseCookie cookie = createRefreshTokenCookie(refreshToken, refreshExpMs / 1000);
            List<String> roles = extractRoles(authentication);
            List<String> permissions = extractPermissions(authentication);

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(new LoginResponse(accessToken, jwtProperties.getAccessTokenExpiration(platform),
                            roles, permissions, refreshToken));
        }

        // Check session limit (throws SessionLimitException if limit reached)
        userSessionService.checkSessionLimit(user.getId(), platform);

        String accessToken = jwtTokenProvider.generateAccessToken(authentication, platform);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication, platform);
        userSessionService.createSession(user, refreshToken, platform, deviceInfo, ip);

        user.setLastLoginDate(LocalDateTime.now());
        user.setErrorLoginCount(0);
        userRepository.save(user);

        long refreshExpMs = jwtProperties.getRefreshTokenExpiration(platform);
        ResponseCookie cookie = createRefreshTokenCookie(refreshToken, refreshExpMs / 1000);
        List<String> roles = extractRoles(authentication);
        List<String> permissions = extractPermissions(authentication);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new LoginResponse(accessToken, jwtProperties.getAccessTokenExpiration(platform),
                        roles, permissions, refreshToken));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(HttpServletRequest request,
                                                 @RequestBody(required = false) Map<String, String> body) {
        String refreshToken = extractRefreshToken(request, body);
        if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String ip = request.getRemoteAddr();

        UserSessionEntity session = userSessionService.validateAndGetSession(refreshToken);
        PlatformType platform = session.getPlatform();

        String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
        var userDetails = userDetailsService.loadUserByUsername(username);

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        String newAccessToken = jwtTokenProvider.generateAccessToken(authToken, platform);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(authToken, platform);

        userSessionService.refreshSession(session, newRefreshToken, ip);

        List<String> roles = extractRoles(authToken);
        List<String> permissions = extractPermissions(authToken);

        long refreshExpMs = jwtProperties.getRefreshTokenExpiration(platform);
        ResponseCookie cookie = createRefreshTokenCookie(newRefreshToken, refreshExpMs / 1000);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new LoginResponse(newAccessToken, jwtProperties.getAccessTokenExpiration(platform),
                        roles, permissions, newRefreshToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request,
                                       @RequestBody(required = false) Map<String, String> body) {
        String refreshToken = extractRefreshToken(request, body);
        String ip = request.getRemoteAddr();

        if (refreshToken != null) {
            userSessionService.revokeSessionByToken(refreshToken, ip);
        }

        ResponseCookie cookie = createRefreshTokenCookie("", 0);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    private PlatformType parsePlatform(String header) {
        try {
            return PlatformType.valueOf(header.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Geçersiz platform: " + header, ErrorCode.INVALID_PLATFORM, HttpStatus.BAD_REQUEST);
        }
    }

    private List<String> extractRoles(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> a.startsWith("ROLE_"))
                .map(a -> a.substring(5))
                .collect(Collectors.toList());
    }

    private List<String> extractPermissions(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> !a.startsWith("ROLE_"))
                .collect(Collectors.toList());
    }

    private ResponseCookie createRefreshTokenCookie(String value, long maxAgeSeconds) {
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE, value)
                .httpOnly(true)
                .secure(true)
                .path(REFRESH_TOKEN_PATH)
                .maxAge(maxAgeSeconds)
                .sameSite("Strict")
                .build();
    }

    private String extractRefreshToken(HttpServletRequest request, Map<String, String> body) {
        // First try cookie
        String fromCookie = extractRefreshTokenFromCookie(request);
        if (fromCookie != null) {
            return fromCookie;
        }
        // Fallback to body (for mobile)
        if (body != null && body.containsKey("refreshToken")) {
            return body.get("refreshToken");
        }
        return null;
    }

    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }
        return Arrays.stream(request.getCookies())
                .filter(c -> REFRESH_TOKEN_COOKIE.equals(c.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }
}
