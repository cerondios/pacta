package com.pacta.pacta_app.payment.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

interface PaymentSpringRepository extends JpaRepository<PaymentJpaEntity, String> {
    Optional<PaymentJpaEntity> findByGatewayTransactionId(String gatewayTransactionId);
    List<PaymentJpaEntity> findByTenantId(String tenantId);
    List<PaymentJpaEntity> findByPropertyRequestId(String propertyRequestId);
}
