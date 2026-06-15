package com.pacta.pacta_app.compliance.domain;

import java.util.List;
import java.util.Optional;

public interface IComplianceDocConfigRepository {
    ComplianceDocConfig save(ComplianceDocConfig config);
    Optional<ComplianceDocConfig> findById(String id);
    Optional<ComplianceDocConfig> findByCountryCodeAndTypeCode(String countryCode, String typeCode);
    List<ComplianceDocConfig> findByCountryCode(String countryCode);
    List<ComplianceDocConfig> findAll();
    void delete(String id);
}
