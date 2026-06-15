package com.pacta.pacta_app.pricing.domain;

import java.util.List;
import java.util.Optional;

public interface IPricingConfigRepository {
    PricingConfig save(PricingConfig config);
    Optional<PricingConfig> findById(String id);
    Optional<PricingConfig> findByCountryCode(String countryCode);
    List<PricingConfig> findAll();
    void delete(String id);
}
