package com.pacta.pacta_app.kyc.infrastructure.persistence;

import com.pacta.pacta_app.kyc.domain.KycDocument;
import com.pacta.pacta_app.kyc.domain.IKycDocumentRepository;
import com.pacta.pacta_app.shared.domain.DocumentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile({"dev", "prod"})
@RequiredArgsConstructor
public class PostgresKycDocumentRepository implements IKycDocumentRepository {

    private final KycDocumentSpringRepository jpa;

    @Override
    public KycDocument save(KycDocument doc) {
        jpa.save(toEntity(doc));
        return doc;
    }

    @Override
    public KycDocument update(KycDocument doc) {
        jpa.save(toEntity(doc));
        return doc;
    }

    @Override
    public Optional<KycDocument> findByUserId(String userId) {
        return jpa.findByUserId(userId).map(this::toDomain);
    }

    @Override
    public List<KycDocument> findAllPendingReview() {
        return jpa.findByStatus(DocumentStatus.PENDING_REVIEW.name())
                .stream().map(this::toDomain).toList();
    }

    private KycDocumentJpaEntity toEntity(KycDocument d) {
        return KycDocumentJpaEntity.builder()
                .id(d.getId()).userId(d.getUserId())
                .frontKey(d.getFrontKey()).rearKey(d.getRearKey()).selfieKey(d.getSelfieKey())
                .status(d.getStatus().name()).submittedAt(d.getSubmittedAt())
                .reviewedBy(d.getReviewedBy()).reviewedAt(d.getReviewedAt())
                .build();
    }

    private KycDocument toDomain(KycDocumentJpaEntity e) {
        return KycDocument.builder()
                .id(e.getId()).userId(e.getUserId())
                .frontKey(e.getFrontKey()).rearKey(e.getRearKey()).selfieKey(e.getSelfieKey())
                .status(DocumentStatus.valueOf(e.getStatus())).submittedAt(e.getSubmittedAt())
                .reviewedBy(e.getReviewedBy()).reviewedAt(e.getReviewedAt())
                .build();
    }
}
