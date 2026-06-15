package com.pacta.pacta_app.deal.application.dto;

import com.pacta.pacta_app.deal.domain.Deal;

public record DealResponse(
        String id,
        String propertyRequestId,
        String propertyId,
        String landlordId,
        String tenantId,
        String status,
        boolean landlordSigned,
        boolean tenantSigned,
        String landlordSignedAt,
        String tenantSignedAt,
        String createdAt
) {
    public static DealResponse from(Deal d) {
        return new DealResponse(
                d.getId(), d.getPropertyRequestId(), d.getPropertyId(), d.getLandlordId(), d.getTenantId(),
                d.getStatus().name(), d.isLandlordSigned(), d.isTenantSigned(),
                d.getLandlordSignedAt(), d.getTenantSignedAt(), d.getCreatedAt());
    }
}
