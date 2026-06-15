package com.pacta.pacta_app.deal.infrastructure.persistence;

import com.pacta.pacta_app.deal.domain.Deal;
import com.pacta.pacta_app.deal.domain.DealStatus;
import com.pacta.pacta_app.deal.domain.IDealRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile({"dev", "prod"})
@RequiredArgsConstructor
public class PostgresDealRepository implements IDealRepository {

    private final DealSpringRepository jpa;

    @Override public Deal save(Deal d)   { jpa.save(toEntity(d)); return d; }
    @Override public Deal update(Deal d) { jpa.save(toEntity(d)); return d; }
    @Override public Optional<Deal> findById(String id) { return jpa.findById(id).map(this::toDomain); }

    @Override
    public Optional<Deal> findByPropertyRequestId(String propertyRequestId) {
        return jpa.findByPropertyRequestId(propertyRequestId).map(this::toDomain);
    }

    @Override
    public List<Deal> findByLandlordId(String landlordId) {
        return jpa.findByLandlordId(landlordId).stream().map(this::toDomain).toList();
    }

    @Override
    public List<Deal> findByTenantId(String tenantId) {
        return jpa.findByTenantId(tenantId).stream().map(this::toDomain).toList();
    }

    private DealJpaEntity toEntity(Deal d) {
        return DealJpaEntity.builder()
                .id(d.getId()).propertyRequestId(d.getPropertyRequestId()).propertyId(d.getPropertyId())
                .landlordId(d.getLandlordId()).tenantId(d.getTenantId())
                .status(d.getStatus().name()).contractFileKey(d.getContractFileKey())
                .landlordSignedAt(d.getLandlordSignedAt()).landlordSignatureName(d.getLandlordSignatureName())
                .tenantSignedAt(d.getTenantSignedAt()).tenantSignatureName(d.getTenantSignatureName())
                .createdAt(d.getCreatedAt()).updatedAt(d.getUpdatedAt())
                .build();
    }

    private Deal toDomain(DealJpaEntity e) {
        return Deal.builder()
                .id(e.getId()).propertyRequestId(e.getPropertyRequestId()).propertyId(e.getPropertyId())
                .landlordId(e.getLandlordId()).tenantId(e.getTenantId())
                .status(DealStatus.valueOf(e.getStatus())).contractFileKey(e.getContractFileKey())
                .landlordSignedAt(e.getLandlordSignedAt()).landlordSignatureName(e.getLandlordSignatureName())
                .tenantSignedAt(e.getTenantSignedAt()).tenantSignatureName(e.getTenantSignatureName())
                .createdAt(e.getCreatedAt()).updatedAt(e.getUpdatedAt())
                .build();
    }
}
