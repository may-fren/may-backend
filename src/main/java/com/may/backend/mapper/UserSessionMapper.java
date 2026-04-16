package com.may.backend.mapper;

import com.may.backend.dto.response.UserSessionResponse;
import com.may.backend.entity.UserSessionEntity;
import org.springframework.stereotype.Component;

@Component
public class UserSessionMapper {

    public UserSessionResponse toResponse(UserSessionEntity entity, boolean currentSession) {
        return UserSessionResponse.builder()
                .id(entity.getId())
                .platform(entity.getPlatform().name())
                .deviceInfo(entity.getDeviceInfo())
                .ipAddress(entity.getIpAddress())
                .loginDate(entity.getLoginDate())
                .lastActivityDate(entity.getLastActivityDate())
                .currentSession(currentSession)
                .build();
    }
}
