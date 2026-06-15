package com.pacta.pacta_app.property.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PropertyAttributeConfigSpringRepository
        extends JpaRepository<PropertyAttributeConfigJpaEntity, String> {

    List<PropertyAttributeConfigJpaEntity> findByPropertyType(String propertyType);

    List<PropertyAttributeConfigJpaEntity> findByEnabledTrue();

    @Query("SELECT c FROM PropertyAttributeConfigJpaEntity c WHERE c.enabled = true AND (c.propertyType = :propertyType OR c.propertyType = 'ALL')")
    List<PropertyAttributeConfigJpaEntity> findEnabledByPropertyType(String propertyType);
}
