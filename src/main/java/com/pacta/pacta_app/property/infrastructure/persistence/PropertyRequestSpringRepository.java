package com.pacta.pacta_app.property.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

interface PropertyRequestSpringRepository extends JpaRepository<PropertyRequestJpaEntity, String> {
    Optional<PropertyRequestJpaEntity> findByTenantIdAndPropertyId(String tenantId, String propertyId);
    List<PropertyRequestJpaEntity> findByTenantId(String tenantId);
    List<PropertyRequestJpaEntity> findByPropertyId(String propertyId);
}
