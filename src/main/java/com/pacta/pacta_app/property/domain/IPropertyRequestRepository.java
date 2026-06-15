package com.pacta.pacta_app.property.domain;

import java.util.List;
import java.util.Optional;

public interface IPropertyRequestRepository {
    PropertyRequest save(PropertyRequest request);
    Optional<PropertyRequest> findByTenantIdAndPropertyId(String tenantId, String propertyId);
    List<PropertyRequest> findByTenantId(String tenantId);
    List<PropertyRequest> findByPropertyId(String propertyId);
    Optional<PropertyRequest> findById(String id);
}
