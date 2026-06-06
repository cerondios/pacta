package com.pacta.pacta_app.reviewer.application.dto;

import com.pacta.pacta_app.kyc.domain.KycDocument;



public record KycReviewItem(
        String id,
        String userId,
        String frontKey,
        String rearKey,
        String selfieKey,
        String status,
        String submittedAt
) {
    public static KycReviewItem from(KycDocument d) {
        return new KycReviewItem(
                d.getId(), d.getUserId(),
                d.getFrontKey(), d.getRearKey(), d.getSelfieKey(),
                d.getStatus().name(), d.getSubmittedAt());
    }
}
