package com.pacta.pacta_app.compliance.infrastructure.persistence;

import com.pacta.pacta_app.compliance.domain.ComplianceDocConfig;
import com.pacta.pacta_app.compliance.domain.IComplianceDocConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile({"dev", "prod"})
@RequiredArgsConstructor
public class PostgresComplianceDocConfigRepository implements IComplianceDocConfigRepository {

    private final ComplianceDocConfigSpringRepository jpa;

    @Override public ComplianceDocConfig save(ComplianceDocConfig c)        { jpa.save(toEntity(c)); return c; }
    @Override public Optional<ComplianceDocConfig> findById(String id)      { return jpa.findById(id).map(this::toDomain); }
    @Override public void delete(String id)                                 { jpa.deleteById(id); }
    @Override public List<ComplianceDocConfig> findAll()                    { return jpa.findAll().stream().map(this::toDomain).toList(); }

    @Override
    public Optional<ComplianceDocConfig> findByCountryCodeAndTypeCode(String countryCode, String typeCode) {
        return jpa.findByCountryCodeAndTypeCode(countryCode, typeCode).map(this::toDomain);
    }

    @Override
    public List<ComplianceDocConfig> findByCountryCode(String countryCode) {
        return jpa.findByCountryCode(countryCode).stream().map(this::toDomain).toList();
    }

    private ComplianceDocConfigJpaEntity toEntity(ComplianceDocConfig c) {
        return ComplianceDocConfigJpaEntity.builder()
                .id(c.getId()).countryCode(c.getCountryCode())
                .typeCode(c.getTypeCode()).displayName(c.getDisplayName())
                .build();
    }

    private ComplianceDocConfig toDomain(ComplianceDocConfigJpaEntity e) {
        return ComplianceDocConfig.builder()
                .id(e.getId()).countryCode(e.getCountryCode())
                .typeCode(e.getTypeCode()).displayName(e.getDisplayName())
                .build();
    }
}
