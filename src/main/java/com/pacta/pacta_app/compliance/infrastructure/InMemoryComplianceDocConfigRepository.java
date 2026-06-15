package com.pacta.pacta_app.compliance.infrastructure;

import com.pacta.pacta_app.compliance.domain.ComplianceDocConfig;
import com.pacta.pacta_app.compliance.domain.IComplianceDocConfigRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Profile({"local", "default"})
public class InMemoryComplianceDocConfigRepository implements IComplianceDocConfigRepository {

    private final Map<String, ComplianceDocConfig> store = new ConcurrentHashMap<>(Map.of(
            "co-antecedentes",  config("co-antecedentes",  "CO", "ANTECEDENTES",        "Certificado de antecedentes"),
            "co-sanciones",     config("co-sanciones",     "CO", "SANCIONES",            "Certificado de sanciones"),
            "co-inhabilidades", config("co-inhabilidades", "CO", "INHABILIDADES",        "Certificado de inhabilidades"),
            "co-historial",     config("co-historial",     "CO", "HISTORIAL_CREDITICIO", "Historial crediticio")
    ));

    private static ComplianceDocConfig config(String id, String country, String typeCode, String displayName) {
        return ComplianceDocConfig.builder()
                .id(id).countryCode(country).typeCode(typeCode).displayName(displayName)
                .build();
    }

    @Override public ComplianceDocConfig save(ComplianceDocConfig c)        { store.put(c.getId(), c); return c; }
    @Override public Optional<ComplianceDocConfig> findById(String id)      { return Optional.ofNullable(store.get(id)); }
    @Override public void delete(String id)                                 { store.remove(id); }

    @Override
    public Optional<ComplianceDocConfig> findByCountryCodeAndTypeCode(String countryCode, String typeCode) {
        return store.values().stream()
                .filter(c -> c.getCountryCode().equals(countryCode) && c.getTypeCode().equals(typeCode))
                .findFirst();
    }

    @Override
    public List<ComplianceDocConfig> findByCountryCode(String countryCode) {
        return store.values().stream()
                .filter(c -> c.getCountryCode().equals(countryCode))
                .toList();
    }

    @Override
    public List<ComplianceDocConfig> findAll() {
        return List.copyOf(store.values());
    }
}
