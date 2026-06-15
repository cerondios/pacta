package com.pacta.pacta_app.deal.domain;

import java.util.List;
import java.util.Optional;

public interface IDealRepository {
    Deal save(Deal deal);
    Deal update(Deal deal);
    Optional<Deal> findById(String id);
    Optional<Deal> findByPropertyRequestId(String propertyRequestId);
    List<Deal> findByLandlordId(String landlordId);
    List<Deal> findByTenantId(String tenantId);
}
