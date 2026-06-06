package com.pacta.pacta_app.kyc.domain;

import java.util.List;
import java.util.Optional;

public interface IKycDocumentRepository {
    KycDocument save(KycDocument doc);
    KycDocument update(KycDocument doc);
    Optional<KycDocument> findByUserId(String userId);
    List<KycDocument> findAllPendingReview();
}
