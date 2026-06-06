package com.pacta.pacta_app.kyc.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

interface KycDocumentSpringRepository extends JpaRepository<KycDocumentJpaEntity, String> {
    Optional<KycDocumentJpaEntity> findByUserId(String userId);
    List<KycDocumentJpaEntity> findByStatus(String status);
}
