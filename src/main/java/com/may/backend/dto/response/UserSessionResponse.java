package com.may.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSessionResponse {
    private Long id;
    private String platform;
    private String deviceInfo;
    private String ipAddress;

    @JsonFormat(pattern = "yyyyMMddHHmmss")
    private LocalDateTime loginDate;

    @JsonFormat(pattern = "yyyyMMddHHmmss")
    private LocalDateTime lastActivityDate;

    private boolean currentSession;
}
