package com.pacta.pacta_app.property.application.dto;

public record PropertyAttributeConfigPatchRequest(
        String  displayName,
        Boolean enabled
) {}
