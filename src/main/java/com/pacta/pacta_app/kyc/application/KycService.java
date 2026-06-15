package com.pacta.pacta_app.kyc.application;

import com.pacta.pacta_app.compliance.application.ComplianceDocumentService;
import com.pacta.pacta_app.kyc.domain.KycDocument;
import com.pacta.pacta_app.kyc.domain.IKycDocumentRepository;
import com.pacta.pacta_app.shared.domain.DocumentStatus;
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

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class KycService {

    private final IKycDocumentRepository    kycDocs;
    private final IUserRepository           users;
    private final MetricRecorder            metrics;
    private final IdGenerator               ids;
    private final StorageService            storage;
    private final ComplianceDocumentService complianceDocumentService;

    @Transactional
    public KycDocument submit(String userId, String frontKey, String rearKey, String selfieKey) {
        Optional<KycDocument> existing = kycDocs.findByUserId(userId);
        if (existing.isPresent()) {
            return resubmit(existing.get(), frontKey, rearKey, selfieKey);
        }
        return create(userId, frontKey, rearKey, selfieKey);
    }

    private KycDocument create(String userId, String frontKey, String rearKey, String selfieKey) {
        KycDocument doc = KycDocument.create(ids, userId, frontKey, rearKey, selfieKey);
        kycDocs.save(doc);
        addScore(userId, 20);
        metrics.incrementCounter("kyc.submitted");
        log.info("KYC submitted userId={}", userId);
        return doc;
    }

    private KycDocument resubmit(KycDocument existing, String frontKey, String rearKey, String selfieKey) {
        if (existing.getStatus() == DocumentStatus.APPROVED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "KYC is already approved");
        }
        if (existing.getStatus() == DocumentStatus.PENDING_REVIEW) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "KYC is already under review");
        }
        KycDocument updated = existing.resubmit(frontKey, rearKey, selfieKey);
        kycDocs.update(updated);
        metrics.incrementCounter("kyc.resubmitted");
        log.info("KYC resubmitted userId={}", existing.getUserId());
        return updated;
    }

    @Transactional
    public KycDocument approve(String userId, String reviewedBy) {
        KycDocument doc     = getByUserIdOrThrow(userId);
        KycDocument updated = doc.approve(reviewedBy);
        kycDocs.update(updated);
        var user = users.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "User not found for KYC approval: " + userId));
        users.update(user.approveKyc(reviewedBy));
        complianceDocumentService.evaluateCompletion(userId, reviewedBy);
        deleteFiles(doc);
        metrics.incrementCounter("kyc.approved");
        return updated;
    }

    @Transactional
    public KycDocument reject(String userId, String reviewedBy) {
        KycDocument doc     = getByUserIdOrThrow(userId);
        KycDocument updated = doc.reject(reviewedBy);
        kycDocs.update(updated);
        deleteFiles(doc);
        metrics.incrementCounter("kyc.rejected");
        return updated;
    }

    public Optional<KycDocument> findByUserId(String userId) {
        return kycDocs.findByUserId(userId);
    }

    public KycDocument getByUserIdOrThrow(String userId) {
        return kycDocs.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "KYC not found for user: " + userId));
    }

    public List<KycDocument> findAllPendingReview() {
        return kycDocs.findAllPendingReview();
    }

    private void deleteFiles(KycDocument doc) {
        try { storage.delete(doc.getFrontKey());  } catch (Exception e) { log.warn("Failed to delete KYC front key {}", doc.getFrontKey(), e); }
        try { storage.delete(doc.getRearKey());   } catch (Exception e) { log.warn("Failed to delete KYC rear key {}", doc.getRearKey(), e); }
        try { storage.delete(doc.getSelfieKey()); } catch (Exception e) { log.warn("Failed to delete KYC selfie key {}", doc.getSelfieKey(), e); }
    }

    private void addScore(String userId, int points) {
        var user = users.findById(userId);
        if (user.isPresent()) {
            users.update(user.get().addScore(points));
        }
    }
}
