package com.pacta.pacta_app.property.infrastructure.persistence;

import com.pacta.pacta_app.property.domain.IPropertyAttributeConfigRepository;
import com.pacta.pacta_app.property.domain.PropertyAttributeConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile({"dev", "prod"})
@RequiredArgsConstructor
public class PostgresPropertyAttributeConfigRepository implements IPropertyAttributeConfigRepository {

    private final PropertyAttributeConfigSpringRepository jpa;

    @Override
    public PropertyAttributeConfig save(PropertyAttributeConfig c) {
        jpa.save(toEntity(c));
        return c;
    }

    @Override public Optional<PropertyAttributeConfig> findById(String id)   { return jpa.findById(id).map(this::toDomain); }
    @Override public List<PropertyAttributeConfig> findAll()                   { return jpa.findAll().stream().map(this::toDomain).toList(); }
    @Override public List<PropertyAttributeConfig> findByPropertyType(String t) { return jpa.findByPropertyType(t).stream().map(this::toDomain).toList(); }
    @Override public List<PropertyAttributeConfig> findEnabled()               { return jpa.findByEnabledTrue().stream().map(this::toDomain).toList(); }
    @Override public List<PropertyAttributeConfig> findEnabledByPropertyType(String t) { return jpa.findEnabledByPropertyType(t).stream().map(this::toDomain).toList(); }
    @Override public void delete(String id)                                    { jpa.deleteById(id); }

    private PropertyAttributeConfigJpaEntity toEntity(PropertyAttributeConfig c) {
        return PropertyAttributeConfigJpaEntity.builder()
                .id(c.getId())
                .propertyType(c.getPropertyType())
                .category(c.getCategory())
                .displayName(c.getDisplayName())
                .enabled(c.isEnabled())
                .createdBy(c.getCreatedBy())
                .createdAt(c.getCreatedAt())
                .build();
    }

    private PropertyAttributeConfig toDomain(PropertyAttributeConfigJpaEntity e) {
        return PropertyAttributeConfig.builder()
                .id(e.getId())
                .propertyType(e.getPropertyType())
                .category(e.getCategory())
                .displayName(e.getDisplayName())
                .enabled(e.isEnabled())
                .createdBy(e.getCreatedBy())
                .createdAt(e.getCreatedAt())
                .build();
    }
}
