package com.pacta.pacta_app.kyc.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "kyc_documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KycDocumentJpaEntity {

    @Id
    private String id;

    @Column(nullable = false, unique = true)
    private String userId;

    @Column(nullable = false)
    private String frontKey;

    @Column(nullable = false)
    private String rearKey;

    @Column(nullable = false)
    private String selfieKey;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private String submittedAt;

    @Column
    private String reviewedBy;

    @Column
    private String reviewedAt;
}
