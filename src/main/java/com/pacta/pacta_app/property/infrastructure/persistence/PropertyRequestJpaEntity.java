package com.pacta.pacta_app.property.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "property_requests")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PropertyRequestJpaEntity {
    @Id private String id;
    @Column(nullable = false) private String tenantId;
    @Column(nullable = false) private String propertyId;
    @Column(nullable = false) private String status;
    @Column(nullable = false) private String appliedAt;
    @Column private String reviewedAt;
    @Column private String reviewedBy;
}
