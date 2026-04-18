package com.may.backend.service;

import com.may.backend.config.JwtProperties;
import com.may.backend.dto.response.UserSessionResponse;
import com.may.backend.entity.UserEntity;
import com.may.backend.entity.UserSessionEntity;
import com.may.backend.enums.PlatformType;
import com.may.backend.enums.SessionStatus;
import com.may.backend.exception.BusinessException;
import com.may.backend.exception.ErrorCode;
import com.may.backend.exception.SessionLimitException;
import com.may.backend.mapper.UserSessionMapper;
import com.may.backend.repository.UserSessionRepository;
import com.may.backend.specification.GenericSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserSessionService {

    private final UserSessionRepository userSessionRepository;
    private final UserSessionMapper userSessionMapper;
    private final JwtProperties jwtProperties;

    @Transactional
    public UserSessionEntity createSession(UserEntity user, String refreshToken, PlatformType platform,
                                           String deviceInfo, String ip) {
        String hash = hashToken(refreshToken);
        LocalDateTime now = LocalDateTime.now();
        long refreshExpMs = jwtProperties.getRefreshTokenExpiration(platform);

        UserSessionEntity session = new UserSessionEntity();
        session.setUser(user);
        session.setRefreshTokenHash(hash);
        session.setPlatform(platform);
        session.setDeviceInfo(deviceInfo);
        session.setIpAddress(ip);
        session.setLoginDate(now);
        session.setLastActivityDate(now);
        session.setExpiresAt(now.plusSeconds(refreshExpMs / 1000));
        session.setStatus(SessionStatus.ACTIVE);
        session.setCreatedBy(user.getUsername());
        session.setCreatedDate(now);
        session.setCreatedIp(ip);

        return userSessionRepository.save(session);
    }

    @Transactional
    public UserSessionEntity createSessionWithForce(UserEntity user, String refreshToken, PlatformType platform,
                                                    String deviceInfo, String ip) {
        revokeOldestSession(user.getId(), platform, ip);
        return createSession(user, refreshToken, platform, deviceInfo, ip);
    }

    @Transactional(readOnly = true)
    public void checkSessionLimit(Long userId, PlatformType platform) {
        int maxSessions = jwtProperties.getMaxSessions(platform);
        List<UserSessionEntity> activeSessions = userSessionRepository
                .findByUserIdAndPlatformAndStatusOrderByLoginDateAsc(userId, platform, SessionStatus.ACTIVE);

        if (activeSessions.size() >= maxSessions) {
            List<UserSessionResponse> sessionResponses = activeSessions.stream()
                    .map(s -> userSessionMapper.toResponse(s, false))
                    .collect(Collectors.toList());
            throw new SessionLimitException(sessionResponses);
        }
    }

    @Transactional(readOnly = true)
    public UserSessionEntity validateAndGetSession(String refreshToken) {
        String hash = hashToken(refreshToken);
        UserSessionEntity session = userSessionRepository
                .findByRefreshTokenHashAndStatus(hash, SessionStatus.ACTIVE)
                .orElseThrow(() -> new BusinessException(
                        "Geçersiz veya süresi dolmuş oturum", ErrorCode.INVALID_REFRESH_TOKEN, HttpStatus.UNAUTHORIZED));

        if (session.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException(
                    "Oturum süresi dolmuş", ErrorCode.INVALID_REFRESH_TOKEN, HttpStatus.UNAUTHORIZED);
        }

        return session;
    }

    @Transactional
    public void refreshSession(UserSessionEntity session, String newRefreshToken, String ip) {
        String newHash = hashToken(newRefreshToken);
        LocalDateTime now = LocalDateTime.now();
        long refreshExpMs = jwtProperties.getRefreshTokenExpiration(session.getPlatform());

        session.setRefreshTokenHash(newHash);
        session.setLastActivityDate(now);
        session.setExpiresAt(now.plusSeconds(refreshExpMs / 1000));
        session.setUpdatedBy(session.getUser().getUsername());
        session.setUpdatedDate(now);
        session.setUpdatedIp(ip);

        userSessionRepository.save(session);
    }

    @Transactional
    public void revokeSessionByToken(String refreshToken, String ip) {
        String hash = hashToken(refreshToken);
        userSessionRepository.findByRefreshTokenHashAndStatus(hash, SessionStatus.ACTIVE)
                .ifPresent(session -> {
                    LocalDateTime now = LocalDateTime.now();
                    session.setStatus(SessionStatus.REVOKED);
                    session.setUpdatedBy(session.getUser().getUsername());
                    session.setUpdatedDate(now);
                    session.setUpdatedIp(ip);
                    userSessionRepository.save(session);
                    log.info("Oturum sonlandirildi: userId={}, platform={}", session.getUser().getId(), session.getPlatform());
                });
    }

    @Transactional
    public void revokeSession(Long sessionId, String username, String ip) {
        UserSessionEntity session = userSessionRepository.findById(sessionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SESSION_NOT_FOUND, HttpStatus.NOT_FOUND));

        if (!session.getUser().getUsername().equals(username)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, HttpStatus.FORBIDDEN);
        }

        LocalDateTime now = LocalDateTime.now();
        session.setStatus(SessionStatus.REVOKED);
        session.setUpdatedBy(username);
        session.setUpdatedDate(now);
        session.setUpdatedIp(ip);
        userSessionRepository.save(session);
        log.info("Oturum manuel sonlandirildi: sessionId={}, username={}", sessionId, username);
    }

    @Transactional(readOnly = true)
    public Page<UserSessionResponse> getActiveSessions(Long userId, String currentRefreshTokenHash,
                                                       Map<String, String> filters, Pageable pageable) {
        filters.put("userId", userId.toString());
        filters.put("status", SessionStatus.ACTIVE.name());
        Specification<UserSessionEntity> spec = GenericSpecification.build(UserSessionEntity.class, filters);

        return userSessionRepository.findAll(spec, pageable)
                .map(s -> userSessionMapper.toResponse(s, s.getRefreshTokenHash().equals(currentRefreshTokenHash)));
    }

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void cleanExpiredSessions() {
        int count = userSessionRepository.expireOldSessions(LocalDateTime.now());
        if (count > 0) {
            log.info("Suresi dolan {} oturum temizlendi", count);
        }
    }

    private void revokeOldestSession(Long userId, PlatformType platform, String ip) {
        List<UserSessionEntity> activeSessions = userSessionRepository
                .findByUserIdAndPlatformAndStatusOrderByLoginDateAsc(userId, platform, SessionStatus.ACTIVE);

        if (!activeSessions.isEmpty()) {
            UserSessionEntity oldest = activeSessions.get(0);
            LocalDateTime now = LocalDateTime.now();
            oldest.setStatus(SessionStatus.REVOKED);
            oldest.setUpdatedBy("SYSTEM");
            oldest.setUpdatedDate(now);
            oldest.setUpdatedIp(ip);
            userSessionRepository.save(oldest);
            log.info("En eski oturum dusuruldu: sessionId={}, userId={}, platform={}",
                    oldest.getId(), userId, platform);
        }
    }

    public String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algoritması bulunamadı", e);
        }
    }
}
