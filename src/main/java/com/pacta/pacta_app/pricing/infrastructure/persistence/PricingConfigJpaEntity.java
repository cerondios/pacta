package com.pacta.pacta_app.pricing.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pricing_configs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PricingConfigJpaEntity {
    @Id
    private String id;

    @Column(nullable = false, unique = true)
    private String countryCode;

    @Column(nullable = false)
    private String commissionPercentage;

    @Column(nullable = false)
    private String createdAt;

    private String updatedAt;
}
