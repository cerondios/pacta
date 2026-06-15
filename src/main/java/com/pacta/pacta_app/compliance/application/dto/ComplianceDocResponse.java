package com.pacta.pacta_app.compliance.application.dto;

import com.pacta.pacta_app.compliance.domain.ComplianceDocument;

public record ComplianceDocResponse(
        String  id,
        String  userId,
        String  typeCode,
        String  key,
        String  issuedAt,
        String  expiresAt,
        String  status,
        boolean expired,
        String  submittedAt
) {
    public static ComplianceDocResponse from(ComplianceDocument d) {
        return new ComplianceDocResponse(
                d.getId(), d.getUserId(), d.getTypeCode(), d.getKey(),
                d.getIssuedAt(), d.getExpiresAt(), d.getStatus().name(),
                d.isExpired(), d.getSubmittedAt());
    }
}
