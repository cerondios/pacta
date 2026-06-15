package com.pacta.pacta_app.pricing.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface PricingConfigSpringRepository extends JpaRepository<PricingConfigJpaEntity, String> {
    Optional<PricingConfigJpaEntity> findByCountryCode(String countryCode);
}
