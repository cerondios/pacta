package com.pacta.pacta_app.kyc.domain;

import com.pacta.pacta_app.shared.domain.DateUtil;
import com.pacta.pacta_app.shared.domain.DocumentStatus;
import com.pacta.pacta_app.shared.domain.IdGenerator;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class KycDocument {

    private final String         id;
    private final String         userId;
    private final String         frontKey;
    private final String         rearKey;
    private final String         selfieKey;
    private final DocumentStatus status;
    private final String         submittedAt;
    private final String         reviewedBy;
    private final String         reviewedAt;

    public static KycDocument create(IdGenerator ids, String userId,
                                     String frontKey, String rearKey, String selfieKey) {
        return KycDocument.builder()
                .id(ids.generate())
                .userId(userId)
                .frontKey(frontKey)
                .rearKey(rearKey)
                .selfieKey(selfieKey)
                .status(DocumentStatus.PENDING_REVIEW)
                .submittedAt(DateUtil.now())
                .build();
    }

    /**
     * User re-uploads documents after a rejection.
     * Only valid from REJECTED — resets to PENDING_REVIEW with new keys.
     */
    public KycDocument resubmit(String frontKey, String rearKey, String selfieKey) {
        if (this.status != DocumentStatus.REJECTED)
            throw new IllegalStateException("KYC can only be resubmitted when REJECTED, current: " + this.status);
        return this.toBuilder()
                .frontKey(frontKey)
                .rearKey(rearKey)
                .selfieKey(selfieKey)
                .status(DocumentStatus.PENDING_REVIEW)
                .submittedAt(DateUtil.now())
                .reviewedBy(null)
                .reviewedAt(null)
                .build();
    }

    public KycDocument approve(String reviewedBy) {
        if (this.status != DocumentStatus.PENDING_REVIEW)
            throw new IllegalStateException("KYC can only be approved when PENDING_REVIEW, current: " + this.status);
        return this.toBuilder()
                .status(DocumentStatus.APPROVED)
                .reviewedBy(reviewedBy)
                .reviewedAt(DateUtil.now())
                .build();
    }

    public KycDocument reject(String reviewedBy) {
        if (this.status != DocumentStatus.PENDING_REVIEW)
            throw new IllegalStateException("KYC can only be rejected when PENDING_REVIEW, current: " + this.status);
        return this.toBuilder()
                .status(DocumentStatus.REJECTED)
                .reviewedBy(reviewedBy)
                .reviewedAt(DateUtil.now())
                .build();
    }
}
