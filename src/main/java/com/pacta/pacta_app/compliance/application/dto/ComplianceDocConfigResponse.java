package com.pacta.pacta_app.compliance.application.dto;

import com.pacta.pacta_app.compliance.domain.ComplianceDocConfig;

public record ComplianceDocConfigResponse(
        String id,
        String countryCode,
        String typeCode,
        String displayName
) {
    public static ComplianceDocConfigResponse from(ComplianceDocConfig c) {
        return new ComplianceDocConfigResponse(c.getId(), c.getCountryCode(), c.getTypeCode(), c.getDisplayName());
    }
}
