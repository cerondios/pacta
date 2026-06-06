package com.pacta.pacta_app.auth.application;

/** Result of a successful register/login: a Bearer JWT and its lifetime. */
public record AuthResponse(String accessToken, String tokenType, long expiresIn) {

    public static AuthResponse bearer(String token, long expiresIn) {
        return new AuthResponse(token, "Bearer", expiresIn);
    }
}
