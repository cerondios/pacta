package com.pacta.pacta_app.deal.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

interface DealSpringRepository extends JpaRepository<DealJpaEntity, String> {
    Optional<DealJpaEntity> findByPropertyRequestId(String propertyRequestId);
    List<DealJpaEntity> findByLandlordId(String landlordId);
    List<DealJpaEntity> findByTenantId(String tenantId);
}
