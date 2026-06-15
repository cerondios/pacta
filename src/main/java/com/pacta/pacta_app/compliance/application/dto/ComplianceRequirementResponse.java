package com.pacta.pacta_app.compliance.application.dto;

import com.pacta.pacta_app.compliance.domain.ComplianceDocConfig;
import com.pacta.pacta_app.compliance.domain.ComplianceDocument;

public record ComplianceRequirementResponse(
        String typeCode,
        String displayName,
        String countryCode,
        String status,
        String docId,
        String expiresAt
) {
    public static ComplianceRequirementResponse pending(ComplianceDocConfig config) {
        return new ComplianceRequirementResponse(
                config.getTypeCode(), config.getDisplayName(), config.getCountryCode(),
                "NOT_SUBMITTED", null, null);
    }

    public static ComplianceRequirementResponse withDoc(ComplianceDocConfig config, ComplianceDocument doc) {
        return new ComplianceRequirementResponse(
                config.getTypeCode(), config.getDisplayName(), config.getCountryCode(),
                doc.getStatus().name(), doc.getId(), doc.getExpiresAt());
    }
}
