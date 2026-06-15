package com.pacta.pacta_app.payment.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "payments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PaymentJpaEntity {
    @Id
    private String id;

    @Column(nullable = false)
    private String tenantId;

    @Column(nullable = false)
    private String propertyRequestId;

    @Column(nullable = false)
    private long amountInCents;

    @Column(nullable = false)
    private String currency;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false, unique = true)
    private String reference;

    private String gatewayTransactionId;

    private String redirectUrl;

    @Column(nullable = false)
    private String createdAt;

    private String updatedAt;
}
