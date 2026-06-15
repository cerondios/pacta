package com.pacta.pacta_app.compliance.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record ComplianceDocRequest(
        @NotBlank String typeCode,
        @NotBlank String key,
        @NotNull  Instant issuedAt
) {}
