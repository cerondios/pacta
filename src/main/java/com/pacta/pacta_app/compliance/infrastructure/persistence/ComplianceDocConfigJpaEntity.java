package com.pacta.pacta_app.compliance.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "document_configs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ComplianceDocConfigJpaEntity {
    @Id
    private String id;

    @Column(nullable = false)
    private String countryCode;

    @Column(nullable = false)
    private String typeCode;

    @Column(nullable = false)
    private String displayName;
}
