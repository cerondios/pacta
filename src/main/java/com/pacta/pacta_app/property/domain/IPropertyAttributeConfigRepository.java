package com.pacta.pacta_app.property.domain;

import java.util.List;
import java.util.Optional;

public interface IPropertyAttributeConfigRepository {
    PropertyAttributeConfig save(PropertyAttributeConfig config);
    Optional<PropertyAttributeConfig> findById(String id);
    List<PropertyAttributeConfig> findAll();
    List<PropertyAttributeConfig> findByPropertyType(String propertyType);
    List<PropertyAttributeConfig> findEnabled();
    List<PropertyAttributeConfig> findEnabledByPropertyType(String propertyType);
    void delete(String id);
}
