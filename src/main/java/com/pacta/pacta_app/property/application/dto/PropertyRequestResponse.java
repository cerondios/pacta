package com.pacta.pacta_app.property.application.dto;

import com.pacta.pacta_app.property.domain.PropertyRequest;

public record PropertyRequestResponse(
        String id,
        String tenant_id,
        String property_id,
        String status,
        String applied_at,
        String reviewed_at,
        String reviewed_by
) {
    public static PropertyRequestResponse from(PropertyRequest r) {
        return new PropertyRequestResponse(
                r.getId(), r.getTenantId(), r.getPropertyId(),
                r.getStatus().name(), r.getAppliedAt(),
                r.getReviewedAt(), r.getReviewedBy()
        );
    }
}
