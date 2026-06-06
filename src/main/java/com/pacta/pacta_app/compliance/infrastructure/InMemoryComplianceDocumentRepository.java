package com.pacta.pacta_app.compliance.infrastructure;

import com.pacta.pacta_app.compliance.domain.ComplianceDocument;
import com.pacta.pacta_app.compliance.domain.DocumentType;
import com.pacta.pacta_app.compliance.domain.IComplianceDocumentRepository;
import com.pacta.pacta_app.shared.domain.DocumentStatus;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Profile({"local", "default"})
public class InMemoryComplianceDocumentRepository implements IComplianceDocumentRepository {

    private final Map<String, ComplianceDocument> store = new ConcurrentHashMap<>();

    @Override
    public ComplianceDocument save(ComplianceDocument doc) {
        store.put(doc.getId(), doc);
        return doc;
    }

    @Override
    public ComplianceDocument update(ComplianceDocument doc) {
        store.put(doc.getId(), doc);
        return doc;
    }

    @Override
    public List<ComplianceDocument> findByUserId(String userId) {
        return store.values().stream().filter(d -> d.getUserId().equals(userId)).toList();
    }

    @Override
    public Optional<ComplianceDocument> findByUserIdAndType(String userId, DocumentType type) {
        return store.values().stream()
                .filter(d -> d.getUserId().equals(userId) && d.getType() == type)
                .findFirst();
    }

    @Override
    public Optional<ComplianceDocument> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<ComplianceDocument> findAllPendingReview() {
        return store.values().stream()
                .filter(d -> d.getStatus() == DocumentStatus.PENDING_REVIEW)
                .toList();
    }

    @Override
    public List<ComplianceDocument> findExpiredAndNotMarked() {
        Instant now = Instant.now();
        return store.values().stream()
                .filter(d -> !d.isExpired() && d.getExpiresAt() != null
                        && Instant.parse(d.getExpiresAt()).isBefore(now))
                .toList();
    }
}
