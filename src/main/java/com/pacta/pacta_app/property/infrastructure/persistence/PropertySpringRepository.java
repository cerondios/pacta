package com.pacta.pacta_app.property.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

interface PropertySpringRepository extends JpaRepository<PropertyJpaEntity, String> {
    List<PropertyJpaEntity> findByLandlordId(String landlordId);
    List<PropertyJpaEntity> findByStatus(String status);
}
