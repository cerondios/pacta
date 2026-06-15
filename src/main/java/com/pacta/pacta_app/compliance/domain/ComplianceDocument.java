package com.pacta.pacta_app.compliance.domain;

import com.pacta.pacta_app.shared.domain.DateUtil;
import com.pacta.pacta_app.shared.domain.DocumentStatus;
import com.pacta.pacta_app.shared.domain.IdGenerator;
import lombok.Builder;
import lombok.Getter;

import java.time.Duration;
import java.time.Instant;

@Getter
@Builder(toBuilder = true)
public class ComplianceDocument {

    private final String         id;
    private final String         userId;
    private final String         typeCode;
    private final String         key;
    private final String         issuedAt;
    private final String         expiresAt;
    private final DocumentStatus status;
    private final boolean        expired;
    private final String         submittedAt;
    private final String         reviewedBy;
    private final String         reviewedAt;

    public boolean isApprovedAndValid() {
        return status == DocumentStatus.APPROVED && !expired;
    }

    public boolean isPendingReview() {
        return status == DocumentStatus.PENDING_REVIEW;
    }

    public ComplianceDocument approve(String reviewedBy) {
        if (this.status != DocumentStatus.PENDING_REVIEW)
            throw new IllegalStateException("Document can only be approved when PENDING_REVIEW, current: " + this.status);
        return this.toBuilder()
                .status(DocumentStatus.APPROVED)
                .reviewedBy(reviewedBy)
                .reviewedAt(DateUtil.now())
                .build();
    }

    public ComplianceDocument reject(String reviewedBy) {
        if (this.status != DocumentStatus.PENDING_REVIEW)
            throw new IllegalStateException("Document can only be rejected when PENDING_REVIEW, current: " + this.status);
        return this.toBuilder()
                .status(DocumentStatus.REJECTED)
                .reviewedBy(reviewedBy)
                .reviewedAt(DateUtil.now())
                .build();
    }

    public ComplianceDocument markExpired() {
        return this.toBuilder().expired(true).build();
    }

    public static ComplianceDocument create(IdGenerator ids, String userId, String typeCode,
                                            String key, Instant issuedAt, int expiryDays) {
        return ComplianceDocument.builder()
                .id(ids.generate())
                .userId(userId)
                .typeCode(typeCode)
                .key(key)
                .issuedAt(DateUtil.format(issuedAt))
                .expiresAt(DateUtil.format(issuedAt.plus(Duration.ofDays(expiryDays))))
                .status(DocumentStatus.PENDING_REVIEW)
                .expired(false)
                .submittedAt(DateUtil.now())
                .build();
    }
}
