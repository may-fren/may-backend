package com.may.backend.entity;

import com.may.backend.entity.base.BaseEntity;
import com.may.backend.enums.PlatformType;
import com.may.backend.enums.SessionStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "USER_SESSION", schema = "MAY")
@Getter
@Setter
public class UserSessionEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_session_seq")
    @SequenceGenerator(name = "user_session_seq", sequenceName = "MAY.SEQ_USER_SESSION", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USERS_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_USER_SESSION_USERS"))
    private UserEntity user;

    @Column(name = "REFRESH_TOKEN_HASH", nullable = false, length = 64)
    private String refreshTokenHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "PLATFORM", nullable = false, length = 20)
    private PlatformType platform;

    @Column(name = "DEVICE_INFO", length = 500)
    private String deviceInfo;

    @Column(name = "IP_ADDRESS", nullable = false, length = 45)
    private String ipAddress;

    @Column(name = "LOGIN_DATE", nullable = false)
    private LocalDateTime loginDate;

    @Column(name = "LAST_ACTIVITY_DATE", nullable = false)
    private LocalDateTime lastActivityDate;

    @Column(name = "EXPIRES_AT", nullable = false)
    private LocalDateTime expiresAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false, length = 20)
    private SessionStatus status = SessionStatus.ACTIVE;
}
