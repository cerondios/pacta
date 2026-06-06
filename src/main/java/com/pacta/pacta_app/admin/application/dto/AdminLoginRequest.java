package com.pacta.pacta_app.admin.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AdminLoginRequest(
        @Email @NotBlank String email,
        @NotBlank String password
) {}
