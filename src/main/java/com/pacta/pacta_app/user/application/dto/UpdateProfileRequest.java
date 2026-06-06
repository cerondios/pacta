package com.pacta.pacta_app.user.application.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateProfileRequest(
        @NotBlank String fullName,
        PhoneDto phone,
        String country
) {}
