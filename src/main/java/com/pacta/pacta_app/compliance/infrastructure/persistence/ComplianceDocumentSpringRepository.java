package com.pacta.pacta_app.compliance.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

interface ComplianceDocumentSpringRepository extends JpaRepository<ComplianceDocumentJpaEntity, String> {
    List<ComplianceDocumentJpaEntity> findByUserId(String userId);
    Optional<ComplianceDocumentJpaEntity> findByUserIdAndDocumentType(String userId, String documentType);
    List<ComplianceDocumentJpaEntity> findByStatus(String status);
    @Query(value = "SELECT * FROM compliance_documents WHERE expires_at::TIMESTAMPTZ < NOW() AND expired = false", nativeQuery = true)
    List<ComplianceDocumentJpaEntity> findExpiredAndNotMarked();
}
