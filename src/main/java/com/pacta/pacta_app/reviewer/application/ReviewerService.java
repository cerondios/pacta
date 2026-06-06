package com.pacta.pacta_app.reviewer.application;

import com.pacta.pacta_app.admin.domain.Admin;
import com.pacta.pacta_app.admin.domain.IAdminRepository;
import com.pacta.pacta_app.compliance.application.ComplianceDocumentService;
import com.pacta.pacta_app.compliance.domain.ComplianceDocument;
import com.pacta.pacta_app.kyc.application.KycService;
import com.pacta.pacta_app.kyc.domain.KycDocument;
import com.pacta.pacta_app.shared.domain.MetricRecorder;
import com.pacta.pacta_app.user.domain.IUserRepository;
import com.pacta.pacta_app.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewerService {

    private final KycService                 kycService;
    private final ComplianceDocumentService  complianceService;
    private final IAdminRepository           adminRepository;
    private final IUserRepository            userRepository;
    private final MetricRecorder             metrics;

    // ── Grouped pending queue ─────────────────────────────────────────────────

    public PendingReviewQueue getPendingQueue() {
        List<KycDocument>         pendingKyc  = kycService.findAllPendingReview();
        List<ComplianceDocument>  pendingDocs = complianceService.findAllPendingReview();

        // Group by userId using a linked map to preserve insertion order
        Map<String, UserReviewRequest> byUser = new LinkedHashMap<>();

        pendingKyc.forEach(kyc -> byUser
                .computeIfAbsent(kyc.getUserId(), this::buildUserRequest)
                .setKyc(kyc));

        pendingDocs.forEach(doc -> byUser
                .computeIfAbsent(doc.getUserId(), this::buildUserRequest)
                .getDocuments().add(doc));

        List<UserReviewRequest> users = List.copyOf(byUser.values());
        int totalItems = pendingKyc.size() + pendingDocs.size();
        return new PendingReviewQueue(users.size(), totalItems, users);
    }

    private UserReviewRequest buildUserRequest(String userId) {
        User user = userRepository.findById(userId).orElse(null);
        return new UserReviewRequest(
                userId,
                user != null ? user.getFullName() : "Unknown",
                user != null ? user.getEmail() : "",
                null,
                new ArrayList<>()
        );
    }

    // ── KYC review ────────────────────────────────────────────────────────────

    @Transactional
    public KycDocument approveKyc(String userId, String reviewerId) {
        validateReviewer(reviewerId);
        KycDocument updated = kycService.approve(userId, reviewerId);
        metrics.incrementCounter("reviewer.kyc.approved");
        return updated;
    }

    @Transactional
    public KycDocument rejectKyc(String userId, String reviewerId) {
        validateReviewer(reviewerId);
        KycDocument updated = kycService.reject(userId, reviewerId);
        metrics.incrementCounter("reviewer.kyc.rejected");
        return updated;
    }

    // ── Compliance document review ────────────────────────────────────────────

    @Transactional
    public ComplianceDocument approveDocument(String docId, String reviewerId) {
        validateReviewer(reviewerId);
        ComplianceDocument updated = complianceService.approve(docId, reviewerId);
        metrics.incrementCounter("reviewer.document.approved");
        log.info("Compliance doc approved docId={} by={}", docId, reviewerId);
        return updated;
    }

    @Transactional
    public ComplianceDocument rejectDocument(String docId, String reviewerId) {
        validateReviewer(reviewerId);
        ComplianceDocument updated = complianceService.reject(docId, reviewerId);
        metrics.incrementCounter("reviewer.document.rejected");
        log.info("Compliance doc rejected docId={} by={}", docId, reviewerId);
        return updated;
    }

    // ── Validation ────────────────────────────────────────────────────────────

    private void validateReviewer(String reviewerId) {
        adminRepository.findById(reviewerId)
                .filter(Admin::canReview)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Not authorized to review"));
    }

    // ── DTOs ──────────────────────────────────────────────────────────────────

    public record PendingReviewQueue(
            int totalUsers,
            int totalItems,
            List<UserReviewRequest> users
    ) {}

    public static class UserReviewRequest {
        private final String               userId;
        private final String               fullName;
        private final String               email;
        private KycDocument                kyc;
        private final List<ComplianceDocument> documents;

        public UserReviewRequest(String userId, String fullName, String email,
                                 KycDocument kyc, List<ComplianceDocument> documents) {
            this.userId    = userId;
            this.fullName  = fullName;
            this.email     = email;
            this.kyc       = kyc;
            this.documents = documents;
        }

        public String getUserId()                        { return userId; }
        public String getFullName()                      { return fullName; }
        public String getEmail()                         { return email; }
        public KycDocument getKyc()                      { return kyc; }
        public void setKyc(KycDocument kyc)              { this.kyc = kyc; }
        public List<ComplianceDocument> getDocuments()   { return documents; }
        public int getPendingCount()                     { return (kyc != null ? 1 : 0) + documents.size(); }
    }
}
