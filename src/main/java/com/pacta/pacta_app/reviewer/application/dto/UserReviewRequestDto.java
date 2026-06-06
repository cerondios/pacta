package com.pacta.pacta_app.reviewer.application.dto;

import com.pacta.pacta_app.kyc.application.dto.KycResponse;
import com.pacta.pacta_app.reviewer.application.ReviewerService;

import java.util.List;

public record UserReviewRequestDto(
        String userId,
        String fullName,
        String email,
        int pendingCount,
        KycResponse kyc,
        List<ComplianceReviewItem> documents
) {
    public static UserReviewRequestDto from(ReviewerService.UserReviewRequest r) {
        return new UserReviewRequestDto(
                r.getUserId(),
                r.getFullName(),
                r.getEmail(),
                r.getPendingCount(),
                r.getKyc() != null ? KycResponse.from(r.getKyc()) : null,
                r.getDocuments().stream().map(ComplianceReviewItem::from).toList()
        );
    }
}
