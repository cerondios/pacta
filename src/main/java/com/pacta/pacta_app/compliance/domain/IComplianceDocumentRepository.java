package com.pacta.pacta_app.compliance.domain;

import java.util.List;
import java.util.Optional;

public interface IComplianceDocumentRepository {
    ComplianceDocument save(ComplianceDocument doc);
    ComplianceDocument update(ComplianceDocument doc);
    List<ComplianceDocument> findByUserId(String userId);
    Optional<ComplianceDocument> findByUserIdAndTypeCode(String userId, String typeCode);
    Optional<ComplianceDocument> findById(String id);
    List<ComplianceDocument> findAllPendingReview();
    List<ComplianceDocument> findExpiredAndNotMarked();
    void delete(String id);
}
