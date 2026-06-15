package com.pacta.pacta_app.compliance.application;

import com.pacta.pacta_app.compliance.application.dto.ComplianceRequirementResponse;
import com.pacta.pacta_app.compliance.domain.ComplianceDocConfig;
import com.pacta.pacta_app.compliance.domain.ComplianceDocument;
import com.pacta.pacta_app.compliance.domain.IComplianceDocConfigRepository;
import com.pacta.pacta_app.compliance.domain.IComplianceDocumentRepository;
import com.pacta.pacta_app.shared.domain.IdGenerator;
import com.pacta.pacta_app.shared.domain.MetricRecorder;
import com.pacta.pacta_app.shared.domain.StorageService;
import com.pacta.pacta_app.user.domain.IUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ComplianceDocumentService {

    private final IComplianceDocumentRepository complianceDocs;
    private final IComplianceDocConfigRepository     docConfigs;
    private final IUserRepository               users;
    private final MetricRecorder                metrics;
    private final IdGenerator                   ids;
    private final StorageService                storage;

    @Transactional
    public ComplianceDocument submit(String userId, String typeCode, String key, Instant issuedAt) {
        var user = users.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + userId));

        docConfigs.findByCountryCodeAndTypeCode(user.getCountry(), typeCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Document type '" + typeCode + "' is not valid for country: " + user.getCountry()));

        var existing = complianceDocs.findByUserIdAndTypeCode(userId, typeCode);
        if (existing.isPresent()) {
            var status = existing.get().getStatus();
            if (status == com.pacta.pacta_app.shared.domain.DocumentStatus.PENDING_REVIEW ||
                status == com.pacta.pacta_app.shared.domain.DocumentStatus.APPROVED)
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Document of type " + typeCode + " already submitted for this user");
            complianceDocs.delete(existing.get().getId());
        }

        ComplianceDocument doc = ComplianceDocument.create(ids, userId, typeCode, key, issuedAt, 90);
        complianceDocs.save(doc);
        metrics.incrementCounter("compliance.submitted", "type", typeCode);
        return doc;
    }

    @Transactional
    public ComplianceDocument approve(String docId, String reviewedBy) {
        ComplianceDocument doc     = findById(docId);
        ComplianceDocument updated = doc.approve(reviewedBy);
        complianceDocs.update(updated);
        deleteFile(doc);
        addScore(doc.getUserId(), 10);
        evaluateCompletion(doc.getUserId(), reviewedBy);
        metrics.incrementCounter("compliance.approved");
        log.info("Compliance doc approved docId={} by={}", docId, reviewedBy);
        return updated;
    }

    @Transactional
    public ComplianceDocument reject(String docId, String reviewedBy) {
        ComplianceDocument doc     = findById(docId);
        ComplianceDocument updated = doc.reject(reviewedBy);
        complianceDocs.update(updated);
        deleteFile(doc);
        metrics.incrementCounter("compliance.rejected");
        log.info("Compliance doc rejected docId={} by={}", docId, reviewedBy);
        return updated;
    }

    @Transactional
    public void delete(String docId, String userId) {
        ComplianceDocument doc = findById(docId);
        if (!doc.getUserId().equals(userId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your document");
        if (doc.getStatus() == com.pacta.pacta_app.shared.domain.DocumentStatus.APPROVED)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot remove an approved document");
        complianceDocs.delete(docId);
        deleteFile(doc);
        metrics.incrementCounter("compliance.deleted", "type", doc.getTypeCode());
    }

    public List<ComplianceDocument> findByUserId(String userId) {
        return complianceDocs.findByUserId(userId);
    }

    public List<ComplianceDocument> findAllPendingReview() {
        return complianceDocs.findAllPendingReview();
    }

    public List<ComplianceRequirementResponse> getRequirements(String userId) {
        var user = users.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + userId));

        Map<String, ComplianceDocument> submittedByTypeCode = complianceDocs.findByUserId(userId).stream()
                .collect(Collectors.toMap(ComplianceDocument::getTypeCode, Function.identity()));

        return docConfigs.findByCountryCode(user.getCountry()).stream()
                .map(config -> {
                    ComplianceDocument doc = submittedByTypeCode.get(config.getTypeCode());
                    return doc != null
                            ? ComplianceRequirementResponse.withDoc(config, doc)
                            : ComplianceRequirementResponse.pending(config);
                })
                .toList();
    }

    private ComplianceDocument findById(String docId) {
        return complianceDocs.findById(docId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found: " + docId));
    }

    private void deleteFile(ComplianceDocument doc) {
        try { storage.delete(doc.getKey()); } catch (Exception e) { log.warn("Failed to delete compliance doc key {}", doc.getKey(), e); }
    }

    private void addScore(String userId, int points) {
        users.findById(userId).ifPresent(u -> users.update(u.addScore(points)));
    }

    /**
     * Re-evaluates whether a user has met every compliance requirement for their
     * country (KYC is checked by the caller via the ACTIVE precondition on
     * {@code User.complete()}). A country with zero required document types is
     * vacuously satisfied, so the user completes as soon as KYC is approved.
     * Only meaningful from ACTIVE — no-op otherwise.
     */
    public void evaluateCompletion(String userId, String updatedBy) {
        users.findById(userId).ifPresent(user -> {
            List<ComplianceDocConfig> requiredConfigs = docConfigs.findByCountryCode(user.getCountry())
                    .stream().toList();

            Set<String> approvedTypeCodes = complianceDocs.findByUserId(userId).stream()
                    .filter(ComplianceDocument::isApprovedAndValid)
                    .map(ComplianceDocument::getTypeCode)
                    .collect(Collectors.toSet());

            boolean allApproved = requiredConfigs.stream()
                    .allMatch(c -> approvedTypeCodes.contains(c.getTypeCode()));

            if (allApproved) {
                try {
                    users.update(user.complete(updatedBy));
                    addScore(userId, 10);
                    metrics.incrementCounter("user.completed");
                } catch (IllegalStateException ignored) {}
            }
        });
    }
}
