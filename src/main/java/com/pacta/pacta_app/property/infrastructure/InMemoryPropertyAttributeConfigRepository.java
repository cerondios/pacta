package com.pacta.pacta_app.property.infrastructure;

import com.pacta.pacta_app.property.domain.IPropertyAttributeConfigRepository;
import com.pacta.pacta_app.property.domain.PropertyAttributeConfig;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Profile({"local", "default"})
public class InMemoryPropertyAttributeConfigRepository implements IPropertyAttributeConfigRepository {

    private final Map<String, PropertyAttributeConfig> store = new ConcurrentHashMap<>();

    @Override public PropertyAttributeConfig save(PropertyAttributeConfig c) { store.put(c.getId(), c); return c; }
    @Override public Optional<PropertyAttributeConfig> findById(String id)   { return Optional.ofNullable(store.get(id)); }
    @Override public List<PropertyAttributeConfig> findAll()                 { return List.copyOf(store.values()); }
    @Override public List<PropertyAttributeConfig> findByPropertyType(String t) {
        return store.values().stream().filter(c -> c.getPropertyType().equals(t)).toList();
    }
    @Override public List<PropertyAttributeConfig> findEnabled() {
        return store.values().stream().filter(PropertyAttributeConfig::isEnabled).toList();
    }
    @Override public List<PropertyAttributeConfig> findEnabledByPropertyType(String t) {
        return store.values().stream()
                .filter(c -> c.isEnabled() && (c.getPropertyType().equals(t) || c.getPropertyType().equals("ALL")))
                .toList();
    }
    @Override public void delete(String id) { store.remove(id); }
}
