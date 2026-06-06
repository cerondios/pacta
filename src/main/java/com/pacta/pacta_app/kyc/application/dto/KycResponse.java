package com.pacta.pacta_app.kyc.application.dto;

import com.pacta.pacta_app.kyc.domain.KycDocument;



public record KycResponse(
        String id,
        String userId,
        String frontKey,
        String rearKey,
        String selfieKey,
        String status,
        String submittedAt
) {
    public static KycResponse from(KycDocument d) {
        return new KycResponse(
                d.getId(), d.getUserId(),
                d.getFrontKey(), d.getRearKey(), d.getSelfieKey(),
                d.getStatus().name(), d.getSubmittedAt());
    }
}
