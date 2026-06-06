package com.pacta.pacta_app.user.application.dto;

import jakarta.validation.constraints.NotBlank;

public record RemoveUserRequest(@NotBlank String reason) {}
