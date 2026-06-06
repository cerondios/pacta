package com.pacta.pacta_app.compliance.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "compliance_documents")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ComplianceDocumentJpaEntity {
    @Id private String id;
    @Column(nullable = false) private String userId;
    @Column(nullable = false) private String documentType;
    @Column(nullable = false) private String key;
    @Column(nullable = false) private String issuedAt;
    @Column(nullable = false) private String expiresAt;
    @Column(nullable = false) private String status;
    @Column(nullable = false) private boolean expired;
    @Column(nullable = false) private String submittedAt;
    @Column private String reviewedBy;
    @Column private String reviewedAt;
}
