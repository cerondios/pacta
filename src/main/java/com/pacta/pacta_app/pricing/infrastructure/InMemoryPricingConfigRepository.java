package com.pacta.pacta_app.pricing.infrastructure;

import com.pacta.pacta_app.pricing.domain.IPricingConfigRepository;
import com.pacta.pacta_app.pricing.domain.PricingConfig;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
@Profile({"local", "default"})
public class InMemoryPricingConfigRepository implements IPricingConfigRepository {

    private final ConcurrentMap<String, PricingConfig> store = new ConcurrentHashMap<>();

    @Override public PricingConfig save(PricingConfig c)        { store.put(c.getId(), c); return c; }
    @Override public Optional<PricingConfig> findById(String id) { return Optional.ofNullable(store.get(id)); }
    @Override public void delete(String id)                      { store.remove(id); }
    @Override public List<PricingConfig> findAll()                { return List.copyOf(store.values()); }

    @Override
    public Optional<PricingConfig> findByCountryCode(String countryCode) {
        return store.values().stream()
                .filter(c -> c.getCountryCode().equals(countryCode))
                .findFirst();
    }
}
