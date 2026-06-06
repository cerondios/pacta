package com.pacta.pacta_app.compliance.application;

import com.pacta.pacta_app.compliance.domain.*;
import com.pacta.pacta_app.shared.domain.IdGenerator;
import com.pacta.pacta_app.shared.domain.MetricRecorder;
import com.pacta.pacta_app.user.domain.IUserRepository;
import com.pacta.pacta_app.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ComplianceDocumentService {

    private final IComplianceDocumentRepository complianceDocs;
    private final IDocumentConfigRepository     docConfigs;
    private final IUserRepository               users;
    private final MetricRecorder               metrics;
    private final IdGenerator                  ids;

    @Transactional
    public ComplianceDocument submit(String userId, DocumentType type, String key, Instant issuedAt) {
        if (complianceDocs.findByUserIdAndType(userId, type).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Document of type " + type + " already submitted for this user");
        }

        DocumentConfig config = docConfigs.findByType(type)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "No config for type: " + type));

        ComplianceDocument doc = ComplianceDocument.create(ids, userId, type, key, issuedAt, config.getExpiryDays());

        complianceDocs.save(doc);
        metrics.incrementCounter("compliance.submitted", "type", type.name());
        log.info("Compliance doc submitted userId={} type={}", userId, type);
        return doc;
    }

    @Transactional
    public ComplianceDocument approve(String docId, String reviewedBy) {
        ComplianceDocument doc     = findById(docId);
        ComplianceDocument updated = doc.approve(reviewedBy);
        complianceDocs.update(updated);
        addScore(doc.getUserId(), 10);
        evaluateCompletedStatus(doc.getUserId(), reviewedBy);
        metrics.incrementCounter("compliance.approved");
        log.info("Compliance doc approved docId={} by={}", docId, reviewedBy);
        return updated;
    }

    @Transactional
    public ComplianceDocument reject(String docId, String reviewedBy) {
        ComplianceDocument doc     = findById(docId);
        ComplianceDocument updated = doc.reject(reviewedBy);
        complianceDocs.update(updated);
        metrics.incrementCounter("compliance.rejected");
        log.info("Compliance doc rejected docId={} by={}", docId, reviewedBy);
        return updated;
    }

    private ComplianceDocument findById(String docId) {
        return complianceDocs.findById(docId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Document not found: " + docId));
    }

    public List<ComplianceDocument> findByUserId(String userId) {
        return complianceDocs.findByUserId(userId);
    }

    public List<ComplianceDocument> findAllPendingReview() {
        return complianceDocs.findAllPendingReview();
    }

    private void addScore(String userId, int points) {
        users.findById(userId).ifPresent(u ->
                users.update(u.addScore(points)));
    }

    private void evaluateCompletedStatus(String userId, String updatedBy) {
        List<ComplianceDocument> docs = complianceDocs.findByUserId(userId);

        boolean allApproved = docs.stream()
                .map(ComplianceDocument::getType)
                .collect(Collectors.toSet())
                .containsAll(Arrays.asList(DocumentType.values()))
                && docs.stream().allMatch(ComplianceDocument::isApprovedAndValid);

        if (allApproved) {
            users.findById(userId).ifPresent(u -> {
                // complete() enforces ACTIVE → COMPLETED transition internally
                try {
                    users.update(u.complete(updatedBy));
                    addScore(userId, 10);
                    metrics.incrementCounter("user.completed");
                } catch (IllegalStateException ignored) {
                    // user is not in ACTIVE state — skip silently
                }
            });
        }
    }
}
