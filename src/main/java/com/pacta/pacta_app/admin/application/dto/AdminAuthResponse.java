package com.pacta.pacta_app.admin.application.dto;

public record AdminAuthResponse(String accessToken, String tokenType, long expiresIn) {

    public static AdminAuthResponse bearer(String token, long expiresIn) {
        return new AdminAuthResponse(token, "Bearer", expiresIn);
    }
}
