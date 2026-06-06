package com.pacta.pacta_app.compliance.domain;

import java.util.List;
import java.util.Optional;

public interface IDocumentConfigRepository {
    DocumentConfig save(DocumentConfig config);
    Optional<DocumentConfig> findByType(DocumentType type);
    List<DocumentConfig> findAll();
}
