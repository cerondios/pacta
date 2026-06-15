package com.pacta.pacta_app.deal.infrastructure;

import com.pacta.pacta_app.deal.domain.Deal;
import com.pacta.pacta_app.deal.domain.IDealRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
@Profile({"local", "default"})
public class InMemoryDealRepository implements IDealRepository {

    private final ConcurrentMap<String, Deal> store = new ConcurrentHashMap<>();

    @Override public Deal save(Deal d)   { store.put(d.getId(), d); return d; }
    @Override public Deal update(Deal d) { store.put(d.getId(), d); return d; }
    @Override public Optional<Deal> findById(String id) { return Optional.ofNullable(store.get(id)); }

    @Override
    public Optional<Deal> findByPropertyRequestId(String propertyRequestId) {
        return store.values().stream().filter(d -> d.getPropertyRequestId().equals(propertyRequestId)).findFirst();
    }

    @Override
    public List<Deal> findByLandlordId(String landlordId) {
        return store.values().stream().filter(d -> d.getLandlordId().equals(landlordId)).toList();
    }

    @Override
    public List<Deal> findByTenantId(String tenantId) {
        return store.values().stream().filter(d -> d.getTenantId().equals(tenantId)).toList();
    }
}
