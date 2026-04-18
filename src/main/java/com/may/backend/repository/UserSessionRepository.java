package com.may.backend.repository;

import com.may.backend.entity.UserSessionEntity;
import com.may.backend.enums.PlatformType;
import com.may.backend.enums.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserSessionRepository extends JpaRepository<UserSessionEntity, Long>, JpaSpecificationExecutor<UserSessionEntity> {

    Optional<UserSessionEntity> findByRefreshTokenHashAndStatus(String refreshTokenHash, SessionStatus status);

    List<UserSessionEntity> findByUserIdAndStatusOrderByLoginDateAsc(Long userId, SessionStatus status);

    List<UserSessionEntity> findByUserIdAndPlatformAndStatusOrderByLoginDateAsc(
            Long userId, PlatformType platform, SessionStatus status);

    @Modifying
    @Query("UPDATE UserSessionEntity s SET s.status = :status, s.updatedBy = :updatedBy, " +
            "s.updatedDate = :now, s.updatedIp = :ip WHERE s.id = :sessionId")
    void updateSessionStatus(@Param("sessionId") Long sessionId,
                             @Param("status") SessionStatus status,
                             @Param("updatedBy") String updatedBy,
                             @Param("now") LocalDateTime now,
                             @Param("ip") String ip);

    @Modifying
    @Query("UPDATE UserSessionEntity s SET s.status = 'EXPIRED' " +
            "WHERE s.status = 'ACTIVE' AND s.expiresAt < :now")
    int expireOldSessions(@Param("now") LocalDateTime now);
}
