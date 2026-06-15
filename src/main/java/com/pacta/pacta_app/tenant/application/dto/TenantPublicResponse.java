package com.pacta.pacta_app.tenant.application.dto;

import com.pacta.pacta_app.tenant.application.TenantService;
import com.pacta.pacta_app.user.domain.User;

import java.util.List;

public record TenantPublicResponse(
        String id,
        String fullName,
        String country,
        String status,
        int score,
        String createdAt,
        String kycStatus,
        List<ComplianceDocSummary> compliance
) {
    public record ComplianceDocSummary(String typeCode, String status) {}

    public static TenantPublicResponse from(TenantService.TenantProfile profile) {
        User u = profile.user();
        return new TenantPublicResponse(
                u.getId(), u.getFullName(),
                u.getCountry(), u.getStatus().name(), u.getScore(), u.getCreatedAt(),
                profile.kyc() != null ? profile.kyc().getStatus().name() : null,
                profile.compliance().stream()
                        .map(d -> new ComplianceDocSummary(d.getTypeCode(), d.getStatus().name()))
                        .toList()
        );
    }
}
