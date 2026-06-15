package com.pacta.pacta_app.pricing.infrastructure.persistence;

import com.pacta.pacta_app.pricing.domain.IPricingConfigRepository;
import com.pacta.pacta_app.pricing.domain.PricingConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile({"dev", "prod"})
@RequiredArgsConstructor
public class PostgresPricingConfigRepository implements IPricingConfigRepository {

    private final PricingConfigSpringRepository jpa;

    @Override public PricingConfig save(PricingConfig c)   { jpa.save(toEntity(c)); return c; }
    @Override public Optional<PricingConfig> findById(String id) { return jpa.findById(id).map(this::toDomain); }
    @Override public void delete(String id)                { jpa.deleteById(id); }
    @Override public List<PricingConfig> findAll()         { return jpa.findAll().stream().map(this::toDomain).toList(); }

    @Override
    public Optional<PricingConfig> findByCountryCode(String countryCode) {
        return jpa.findByCountryCode(countryCode).map(this::toDomain);
    }

    private PricingConfigJpaEntity toEntity(PricingConfig c) {
        return PricingConfigJpaEntity.builder()
                .id(c.getId()).countryCode(c.getCountryCode())
                .commissionPercentage(String.valueOf(c.getCommissionPercentage()))
                .createdAt(c.getCreatedAt()).updatedAt(c.getUpdatedAt())
                .build();
    }

    private PricingConfig toDomain(PricingConfigJpaEntity e) {
        return PricingConfig.builder()
                .id(e.getId()).countryCode(e.getCountryCode())
                .commissionPercentage(Double.parseDouble(e.getCommissionPercentage()))
                .createdAt(e.getCreatedAt()).updatedAt(e.getUpdatedAt())
                .build();
    }
}
