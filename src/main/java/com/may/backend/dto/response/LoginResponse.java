package com.may.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponse {
    private String accessToken;
    private String tokenType;
    private long expiresIn;
    private List<String> roles;
    private List<String> permissions;
    private String refreshToken;

    public LoginResponse(String accessToken, long expiresIn, List<String> roles, List<String> permissions) {
        this.accessToken = accessToken;
        this.tokenType = "Bearer";
        this.expiresIn = expiresIn;
        this.roles = roles;
        this.permissions = permissions;
        this.refreshToken = null;
    }

    public LoginResponse(String accessToken, long expiresIn, List<String> roles, List<String> permissions, String refreshToken) {
        this.accessToken = accessToken;
        this.tokenType = "Bearer";
        this.expiresIn = expiresIn;
        this.roles = roles;
        this.permissions = permissions;
        this.refreshToken = refreshToken;
    }
}
