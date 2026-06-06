package com.pacta.pacta_app.compliance.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "document_configs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DocumentConfigJpaEntity {
    @Id private String documentType;
    @Column(nullable = false) private int expiryDays;
    @Column(nullable = false) private int warningDays;
}
