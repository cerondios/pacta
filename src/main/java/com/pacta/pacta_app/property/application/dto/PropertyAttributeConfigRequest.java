package com.pacta.pacta_app.property.application.dto;

import jakarta.validation.constraints.NotBlank;

public record PropertyAttributeConfigRequest(
        @NotBlank String propertyType,
        @NotBlank String category,
        @NotBlank String displayName
) {}
