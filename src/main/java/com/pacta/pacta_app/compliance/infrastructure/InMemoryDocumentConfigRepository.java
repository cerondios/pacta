package com.pacta.pacta_app.compliance.infrastructure;

import com.pacta.pacta_app.compliance.domain.DocumentConfig;
import com.pacta.pacta_app.compliance.domain.DocumentType;
import com.pacta.pacta_app.compliance.domain.IDocumentConfigRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Profile({"local", "default"})
public class InMemoryDocumentConfigRepository implements IDocumentConfigRepository {

    // Preloaded with the same defaults as the DB migration seed
    private final Map<String, DocumentConfig> store = new ConcurrentHashMap<>(Map.of(
            DocumentType.ANTECEDENTES.name(),         DocumentConfig.builder().documentType(DocumentType.ANTECEDENTES).expiryDays(90).warningDays(15).build(),
            DocumentType.SANCIONES.name(),            DocumentConfig.builder().documentType(DocumentType.SANCIONES).expiryDays(90).warningDays(15).build(),
            DocumentType.INHABILIDADES.name(),        DocumentConfig.builder().documentType(DocumentType.INHABILIDADES).expiryDays(90).warningDays(15).build(),
            DocumentType.HISTORIAL_CREDITICIO.name(), DocumentConfig.builder().documentType(DocumentType.HISTORIAL_CREDITICIO).expiryDays(180).warningDays(30).build()
    ));

    @Override
    public DocumentConfig save(DocumentConfig config) {
        store.put(config.getDocumentType().name(), config);
        return config;
    }

    @Override
    public Optional<DocumentConfig> findByType(DocumentType type) {
        return Optional.ofNullable(store.get(type.name()));
    }

    @Override
    public List<DocumentConfig> findAll() {
        return List.copyOf(store.values());
    }
}
