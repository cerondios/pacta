package com.pacta.pacta_app.reviewer.application.dto;

import com.pacta.pacta_app.compliance.domain.ComplianceDocument;



public record ComplianceReviewItem(
        String id,
        String userId,
        String type,
        String key,
        String issuedAt,
        String expiresAt,
        String status,
        String submittedAt
) {
    public static ComplianceReviewItem from(ComplianceDocument d) {
        return new ComplianceReviewItem(
                d.getId(), d.getUserId(),
                d.getType().name(), d.getKey(),
                d.getIssuedAt(), d.getExpiresAt(),
                d.getStatus().name(), d.getSubmittedAt());
    }
}
