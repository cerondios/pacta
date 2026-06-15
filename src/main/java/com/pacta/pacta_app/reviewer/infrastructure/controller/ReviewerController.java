package com.pacta.pacta_app.reviewer.infrastructure.controller;

import com.pacta.pacta_app.property.application.PropertyService;
import com.pacta.pacta_app.property.application.dto.PropertyResponse;
import com.pacta.pacta_app.shared.domain.StorageService;
import com.pacta.pacta_app.reviewer.application.ReviewerService;
import com.pacta.pacta_app.reviewer.application.dto.ComplianceReviewItem;
import com.pacta.pacta_app.reviewer.application.dto.KycReviewItem;
import com.pacta.pacta_app.reviewer.application.dto.UserReviewRequestDto;
import com.pacta.pacta_app.shared.infrastructure.filter.OperatorTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/reviewer")
@RequiredArgsConstructor
public class ReviewerController {

    private final ReviewerService reviewerService;
    private final PropertyService propertyService;
    private final StorageService  storageService;

    /**
     * All pending review requests grouped by user.
     * Each entry shows the user's KYC (if pending) and their pending compliance documents.
     */
    @GetMapping("/pending")
    public List<UserReviewRequestDto> getPendingQueue() {
        return reviewerService.getPendingQueue().users().stream()
                .map(UserReviewRequestDto::from)
                .toList();
    }

    // ── KYC ───────────────────────────────────────────────────────────────────

    @PatchMapping("/kyc/{userId}/approve")
    public KycReviewItem approveKyc(@PathVariable String userId,
                                    @RequestHeader(OperatorTokenFilter.HEADER_OPERATOR_ID) String reviewerId) {
        return KycReviewItem.from(reviewerService.approveKyc(userId, reviewerId));
    }

    @PatchMapping("/kyc/{userId}/reject")
    public KycReviewItem rejectKyc(@PathVariable String userId,
                                   @RequestHeader(OperatorTokenFilter.HEADER_OPERATOR_ID) String reviewerId) {
        return KycReviewItem.from(reviewerService.rejectKyc(userId, reviewerId));
    }

    // ── Compliance documents ──────────────────────────────────────────────────

    @PatchMapping("/documents/{docId}/approve")
    public ComplianceReviewItem approveDocument(@PathVariable String docId,
                                                @RequestHeader(OperatorTokenFilter.HEADER_OPERATOR_ID) String reviewerId) {
        return ComplianceReviewItem.from(reviewerService.approveDocument(docId, reviewerId));
    }

    @PatchMapping("/documents/{docId}/reject")
    public ComplianceReviewItem rejectDocument(@PathVariable String docId,
                                               @RequestHeader(OperatorTokenFilter.HEADER_OPERATOR_ID) String reviewerId) {
        return ComplianceReviewItem.from(reviewerService.rejectDocument(docId, reviewerId));
    }

    // ── Properties ────────────────────────────────────────────────────────────

    @GetMapping("/properties")
    public List<PropertyResponse> getAllProperties() {
        return propertyService.findAll().stream()
                .map(p -> PropertyResponse.from(p, storageService))
                .toList();
    }

    @GetMapping("/properties/pending")
    public List<PropertyResponse> getPendingProperties() {
        return propertyService.findPendingReview().stream()
                .map(p -> PropertyResponse.from(p, storageService))
                .toList();
    }

    @PatchMapping("/properties/{id}/approve")
    public PropertyResponse approveProperty(@PathVariable String id,
                                            @RequestHeader(OperatorTokenFilter.HEADER_OPERATOR_ID) String reviewerId) {
        return PropertyResponse.from(propertyService.approve(id, reviewerId), storageService);
    }

    @PatchMapping("/properties/{id}/reject")
    public PropertyResponse rejectProperty(@PathVariable String id,
                                           @RequestHeader(OperatorTokenFilter.HEADER_OPERATOR_ID) String reviewerId) {
        return PropertyResponse.from(propertyService.reject(id, reviewerId), storageService);
    }
}
