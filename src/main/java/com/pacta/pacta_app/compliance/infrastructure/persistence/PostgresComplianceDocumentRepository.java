package com.pacta.pacta_app.compliance.infrastructure.persistence;

import com.pacta.pacta_app.compliance.domain.*;
import com.pacta.pacta_app.shared.domain.DocumentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile({"dev", "prod"})
@RequiredArgsConstructor
public class PostgresComplianceDocumentRepository implements IComplianceDocumentRepository {

    private final ComplianceDocumentSpringRepository jpa;

    @Override public ComplianceDocument save(ComplianceDocument d) { jpa.save(toEntity(d)); return d; }
    @Override public ComplianceDocument update(ComplianceDocument d) { jpa.save(toEntity(d)); return d; }
    @Override public List<ComplianceDocument> findByUserId(String userId) { return jpa.findByUserId(userId).stream().map(this::toDomain).toList(); }
    @Override public Optional<ComplianceDocument> findByUserIdAndType(String userId, DocumentType type) { return jpa.findByUserIdAndDocumentType(userId, type.name()).map(this::toDomain); }
    @Override public Optional<ComplianceDocument> findById(String id) { return jpa.findById(id).map(this::toDomain); }
    @Override public List<ComplianceDocument> findAllPendingReview() { return jpa.findByStatus(DocumentStatus.PENDING_REVIEW.name()).stream().map(this::toDomain).toList(); }
    @Override public List<ComplianceDocument> findExpiredAndNotMarked() { return jpa.findExpiredAndNotMarked().stream().map(this::toDomain).toList(); }

    private ComplianceDocumentJpaEntity toEntity(ComplianceDocument d) {
        return ComplianceDocumentJpaEntity.builder()
                .id(d.getId()).userId(d.getUserId()).documentType(d.getType().name())
                .key(d.getKey()).issuedAt(d.getIssuedAt()).expiresAt(d.getExpiresAt())
                .status(d.getStatus().name()).expired(d.isExpired()).submittedAt(d.getSubmittedAt())
                .reviewedBy(d.getReviewedBy()).reviewedAt(d.getReviewedAt())
                .build();
    }

    private ComplianceDocument toDomain(ComplianceDocumentJpaEntity e) {
        return ComplianceDocument.builder()
                .id(e.getId()).userId(e.getUserId()).type(DocumentType.valueOf(e.getDocumentType()))
                .key(e.getKey()).issuedAt(e.getIssuedAt()).expiresAt(e.getExpiresAt())
                .status(DocumentStatus.valueOf(e.getStatus())).expired(e.isExpired()).submittedAt(e.getSubmittedAt())
                .reviewedBy(e.getReviewedBy()).reviewedAt(e.getReviewedAt())
                .build();
    }
}
