package com.pacta.pacta_app.property.infrastructure;

import com.pacta.pacta_app.property.domain.IPropertyRepository;
import com.pacta.pacta_app.property.domain.Property;
import com.pacta.pacta_app.property.domain.PropertyStatus;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Profile({"local", "default"})
public class InMemoryPropertyRepository implements IPropertyRepository {

    private final Map<String, Property> store = new ConcurrentHashMap<>();

    @Override
    public Property save(Property property) {
        store.put(property.getId(), property);
        return property;
    }

    @Override
    public Optional<Property> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Property> findByLandlordId(String landlordId) {
        return store.values().stream()
                .filter(p -> p.getLandlordId().equals(landlordId))
                .toList();
    }

    @Override
    public List<Property> findByStatus(PropertyStatus status) {
        return store.values().stream()
                .filter(p -> p.getStatus() == status)
                .toList();
    }
}
