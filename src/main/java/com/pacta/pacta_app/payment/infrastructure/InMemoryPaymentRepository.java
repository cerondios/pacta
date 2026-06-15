package com.pacta.pacta_app.payment.infrastructure;

import com.pacta.pacta_app.payment.domain.IPaymentRepository;
import com.pacta.pacta_app.payment.domain.Payment;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
@Profile({"local", "default"})
public class InMemoryPaymentRepository implements IPaymentRepository {

    private final ConcurrentMap<String, Payment> store = new ConcurrentHashMap<>();

    @Override public Payment save(Payment p)   { store.put(p.getId(), p); return p; }
    @Override public Payment update(Payment p) { store.put(p.getId(), p); return p; }
    @Override public Optional<Payment> findById(String id) { return Optional.ofNullable(store.get(id)); }

    @Override
    public Optional<Payment> findByGatewayTransactionId(String gatewayTransactionId) {
        return store.values().stream()
                .filter(p -> gatewayTransactionId.equals(p.getGatewayTransactionId()))
                .findFirst();
    }

    @Override
    public List<Payment> findByTenantId(String tenantId) {
        return store.values().stream().filter(p -> p.getTenantId().equals(tenantId)).toList();
    }

    @Override
    public List<Payment> findByPropertyRequestId(String propertyRequestId) {
        return store.values().stream().filter(p -> p.getPropertyRequestId().equals(propertyRequestId)).toList();
    }
}
