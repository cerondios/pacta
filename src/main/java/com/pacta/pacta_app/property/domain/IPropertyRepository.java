package com.pacta.pacta_app.property.domain;

import com.pacta.pacta_app.property.application.dto.PropertySearchRequest;
import com.pacta.pacta_app.shared.domain.PagedResult;

import java.util.List;
import java.util.Optional;

public interface IPropertyRepository {
    Property save(Property property);
    Optional<Property> findById(String id);
    List<Property> findByLandlordId(String landlordId);
    List<Property> findByLandlordIdExcludingStatus(String landlordId, PropertyStatus excluded);
    List<Property> findByStatus(PropertyStatus status);
    PagedResult<Property> search(PropertySearchRequest req);
    List<Property> findAll();
    void deleteById(String id);
}
