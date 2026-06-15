package com.pacta.pacta_app.property.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "property_attribute_configs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PropertyAttributeConfigJpaEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String propertyType;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String displayName;

    @Column(nullable = false)
    private boolean enabled;

    @Column(nullable = false)
    private String createdBy;

    @Column(nullable = false)
    private String createdAt;
}
