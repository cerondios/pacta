package com.pacta.pacta_app.pricing.application.dto;

/** Result of applying the Pacta commission to a monthly rent amount. */
public record PricingBreakdown(
        long   monthlyRent,
        double commissionPercentage,
        long   pactaFee,
        long   landlordReceives
) {}
