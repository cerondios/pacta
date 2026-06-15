package com.pacta.pacta_app.payment.infrastructure.persistence;

import com.pacta.pacta_app.payment.domain.IPaymentRepository;
import com.pacta.pacta_app.payment.domain.Payment;
import com.pacta.pacta_app.payment.domain.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile({"dev", "prod"})
@RequiredArgsConstructor
public class PostgresPaymentRepository implements IPaymentRepository {

    private final PaymentSpringRepository jpa;

    @Override public Payment save(Payment p)   { jpa.save(toEntity(p)); return p; }
    @Override public Payment update(Payment p) { jpa.save(toEntity(p)); return p; }
    @Override public Optional<Payment> findById(String id) { return jpa.findById(id).map(this::toDomain); }

    @Override
    public Optional<Payment> findByGatewayTransactionId(String gatewayTransactionId) {
        return jpa.findByGatewayTransactionId(gatewayTransactionId).map(this::toDomain);
    }

    @Override
    public List<Payment> findByTenantId(String tenantId) {
        return jpa.findByTenantId(tenantId).stream().map(this::toDomain).toList();
    }

    @Override
    public List<Payment> findByPropertyRequestId(String propertyRequestId) {
        return jpa.findByPropertyRequestId(propertyRequestId).stream().map(this::toDomain).toList();
    }

    private PaymentJpaEntity toEntity(Payment p) {
        return PaymentJpaEntity.builder()
                .id(p.getId()).tenantId(p.getTenantId()).propertyRequestId(p.getPropertyRequestId())
                .amountInCents(p.getAmountInCents()).currency(p.getCurrency())
                .status(p.getStatus().name()).reference(p.getReference())
                .gatewayTransactionId(p.getGatewayTransactionId()).redirectUrl(p.getRedirectUrl())
                .createdAt(p.getCreatedAt()).updatedAt(p.getUpdatedAt())
                .build();
    }

    private Payment toDomain(PaymentJpaEntity e) {
        return Payment.builder()
                .id(e.getId()).tenantId(e.getTenantId()).propertyRequestId(e.getPropertyRequestId())
                .amountInCents(e.getAmountInCents()).currency(e.getCurrency())
                .status(PaymentStatus.valueOf(e.getStatus())).reference(e.getReference())
                .gatewayTransactionId(e.getGatewayTransactionId()).redirectUrl(e.getRedirectUrl())
                .createdAt(e.getCreatedAt()).updatedAt(e.getUpdatedAt())
                .build();
    }
}
