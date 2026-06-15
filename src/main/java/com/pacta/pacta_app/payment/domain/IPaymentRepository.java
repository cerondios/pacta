package com.pacta.pacta_app.payment.domain;

import java.util.List;
import java.util.Optional;

public interface IPaymentRepository {
    Payment save(Payment payment);
    Payment update(Payment payment);
    Optional<Payment> findById(String id);
    Optional<Payment> findByGatewayTransactionId(String gatewayTransactionId);
    List<Payment> findByTenantId(String tenantId);
    List<Payment> findByPropertyRequestId(String propertyRequestId);
}
