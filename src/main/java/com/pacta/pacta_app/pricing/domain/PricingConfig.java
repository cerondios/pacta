package com.pacta.pacta_app.pricing.domain;

import lombok.Builder;
import lombok.Getter;

/** Per-country Pacta commission rate on the monthly rent. */
@Getter
@Builder(toBuilder = true)
public class PricingConfig {
    private final String id;
    private final String countryCode;
    private final double commissionPercentage;
    private final String createdAt;
    private final String updatedAt;
}
