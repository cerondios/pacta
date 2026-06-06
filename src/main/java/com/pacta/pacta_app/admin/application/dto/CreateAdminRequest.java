package com.pacta.pacta_app.admin.application.dto;

import com.pacta.pacta_app.admin.domain.AdminRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateAdminRequest(
        @NotBlank String fullName,
        @Email @NotBlank String email,
        @NotNull AdminRole role,
        @NotBlank String password
) {}
