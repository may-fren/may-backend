package com.may.backend.exception;

import com.may.backend.dto.response.UserSessionResponse;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
public class SessionLimitException extends BusinessException {

    private final List<UserSessionResponse> activeSessions;

    public SessionLimitException(List<UserSessionResponse> activeSessions) {
        super("Bu platformda aktif oturum limitine ulaşıldı", ErrorCode.SESSION_LIMIT_REACHED, HttpStatus.CONFLICT);
        this.activeSessions = activeSessions;
    }
}
