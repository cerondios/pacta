package com.pacta.pacta_app.deal.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "deals")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DealJpaEntity {
    @Id
    private String id;

    @Column(nullable = false, unique = true)
    private String propertyRequestId;

    @Column(nullable = false)
    private String propertyId;

    @Column(nullable = false)
    private String landlordId;

    @Column(nullable = false)
    private String tenantId;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private String contractFileKey;

    private String landlordSignedAt;
    private String landlordSignatureName;
    private String tenantSignedAt;
    private String tenantSignatureName;

    @Column(nullable = false)
    private String createdAt;

    private String updatedAt;
}
