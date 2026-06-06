package com.pacta.pacta_app.auth.infrastructure.controller;

import com.pacta.pacta_app.user.domain.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.HashSet;
import java.util.Set;

public record RegisterRequest(
        @NotBlank @Email String email,
        @NotBlank String fullName,
        PhoneRequest phone,
        String country
) {
    public record PhoneRequest(String indicative, String number) {}
}
