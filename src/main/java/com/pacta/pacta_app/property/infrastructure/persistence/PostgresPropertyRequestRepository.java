package com.pacta.pacta_app.property.infrastructure.persistence;

import com.pacta.pacta_app.property.domain.IPropertyRequestRepository;
import com.pacta.pacta_app.property.domain.PropertyRequest;
import com.pacta.pacta_app.property.domain.PropertyRequestStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile({"dev", "prod"})
@RequiredArgsConstructor
public class PostgresPropertyRequestRepository implements IPropertyRequestRepository {

    private final PropertyRequestSpringRepository jpa;

    @Override public PropertyRequest save(PropertyRequest r) { jpa.save(toEntity(r)); return r; }

    @Override
    public Optional<PropertyRequest> findByTenantIdAndPropertyId(String tenantId, String propertyId) {
        return jpa.findByTenantIdAndPropertyId(tenantId, propertyId).map(this::toDomain);
    }

    @Override
    public List<PropertyRequest> findByTenantId(String tenantId) {
        return jpa.findByTenantId(tenantId).stream().map(this::toDomain).toList();
    }

    @Override
    public List<PropertyRequest> findByPropertyId(String propertyId) {
        return jpa.findByPropertyId(propertyId).stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<PropertyRequest> findById(String id) {
        return jpa.findById(id).map(this::toDomain);
    }

    private PropertyRequestJpaEntity toEntity(PropertyRequest r) {
        return PropertyRequestJpaEntity.builder()
                .id(r.getId()).tenantId(r.getTenantId()).propertyId(r.getPropertyId())
                .status(r.getStatus().name()).appliedAt(r.getAppliedAt())
                .reviewedAt(r.getReviewedAt()).reviewedBy(r.getReviewedBy())
                .build();
    }

    private PropertyRequest toDomain(PropertyRequestJpaEntity e) {
        return PropertyRequest.builder()
                .id(e.getId()).tenantId(e.getTenantId()).propertyId(e.getPropertyId())
                .status(PropertyRequestStatus.valueOf(e.getStatus())).appliedAt(e.getAppliedAt())
                .reviewedAt(e.getReviewedAt()).reviewedBy(e.getReviewedBy())
                .build();
    }
}
