package com.may.backend.config;

import com.may.backend.enums.PlatformType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
public class JwtProperties {
    private String secret;
    private long accessTokenExpiration;
    private long refreshTokenExpiration;
    private Map<String, PlatformTokenConfig> platform = new HashMap<>();

    @Getter
    @Setter
    public static class PlatformTokenConfig {
        private long accessTokenExpiration;
        private long refreshTokenExpiration;
        private int maxSessions = 1;
    }

    public long getAccessTokenExpiration(PlatformType platformType) {
        PlatformTokenConfig config = platform.get(platformType.name().toLowerCase());
        return config != null ? config.getAccessTokenExpiration() : accessTokenExpiration;
    }

    public long getRefreshTokenExpiration(PlatformType platformType) {
        PlatformTokenConfig config = platform.get(platformType.name().toLowerCase());
        return config != null ? config.getRefreshTokenExpiration() : refreshTokenExpiration;
    }

    public int getMaxSessions(PlatformType platformType) {
        PlatformTokenConfig config = platform.get(platformType.name().toLowerCase());
        return config != null ? config.getMaxSessions() : 1;
    }
}
