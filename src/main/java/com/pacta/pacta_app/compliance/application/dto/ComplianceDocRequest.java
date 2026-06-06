package com.pacta.pacta_app.compliance.application.dto;

import com.pacta.pacta_app.compliance.domain.DocumentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record ComplianceDocRequest(
        @NotNull DocumentType type,
        @NotBlank String key,
        @NotNull Instant issuedAt
) {}
