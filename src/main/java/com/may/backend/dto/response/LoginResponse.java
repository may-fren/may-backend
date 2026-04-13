package com.may.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponse {
    private String accessToken;
    private String tokenType;
    private long expiresIn;
    private List<String> roles;
    private List<String> permissions;

    public LoginResponse(String accessToken, long expiresIn, List<String> roles, List<String> permissions) {
        this.accessToken = accessToken;
        this.tokenType = "Bearer";
        this.expiresIn = expiresIn;
        this.roles = roles;
        this.permissions = permissions;
    }
}
