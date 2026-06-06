package com.pacta.pacta_app.kyc.infrastructure;

import com.pacta.pacta_app.kyc.domain.IKycDocumentRepository;
import com.pacta.pacta_app.kyc.domain.KycDocument;
import com.pacta.pacta_app.shared.domain.DocumentStatus;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Profile({"local", "default"})
public class InMemoryKycDocumentRepository implements IKycDocumentRepository {

    private final Map<String, KycDocument> store = new ConcurrentHashMap<>();

    @Override
    public KycDocument save(KycDocument doc) {
        store.put(doc.getUserId(), doc);
        return doc;
    }

    @Override
    public KycDocument update(KycDocument doc) {
        store.put(doc.getUserId(), doc);
        return doc;
    }

    @Override
    public Optional<KycDocument> findByUserId(String userId) {
        return Optional.ofNullable(store.get(userId));
    }

    @Override
    public List<KycDocument> findAllPendingReview() {
        return store.values().stream()
                .filter(d -> d.getStatus() == DocumentStatus.PENDING_REVIEW)
                .toList();
    }
}
