package com.pacta.pacta_app.property.domain;

import java.util.List;
import java.util.Optional;

public interface IPropertyRepository {
    Property save(Property property);
    Optional<Property> findById(String id);
    List<Property> findByLandlordId(String landlordId);
    List<Property> findByStatus(PropertyStatus status);
}
