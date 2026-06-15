package com.pacta.pacta_app.compliance.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

interface ComplianceDocConfigSpringRepository extends JpaRepository<ComplianceDocConfigJpaEntity, String> {
    List<ComplianceDocConfigJpaEntity> findByCountryCode(String countryCode);
    Optional<ComplianceDocConfigJpaEntity> findByCountryCodeAndTypeCode(String countryCode, String typeCode);
}
