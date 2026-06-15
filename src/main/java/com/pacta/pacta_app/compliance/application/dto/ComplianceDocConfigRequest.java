package com.pacta.pacta_app.compliance.application.dto;

import jakarta.validation.constraints.NotBlank;

public record ComplianceDocConfigRequest(
        @NotBlank String countryCode,
        @NotBlank String typeCode,
        @NotBlank String displayName
) {}
