package com.pacta.pacta_app.compliance.infrastructure.persistence;

import com.pacta.pacta_app.compliance.domain.DocumentConfig;
import com.pacta.pacta_app.compliance.domain.IDocumentConfigRepository;
import com.pacta.pacta_app.compliance.domain.DocumentType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile({"dev", "prod"})
@RequiredArgsConstructor
public class PostgresDocumentConfigRepository implements IDocumentConfigRepository {

    private final DocumentConfigSpringRepository jpa;

    @Override public DocumentConfig save(DocumentConfig c) { jpa.save(toEntity(c)); return c; }
    @Override public Optional<DocumentConfig> findByType(DocumentType type) { return jpa.findById(type.name()).map(this::toDomain); }
    @Override public List<DocumentConfig> findAll() { return jpa.findAll().stream().map(this::toDomain).toList(); }

    private DocumentConfigJpaEntity toEntity(DocumentConfig c) {
        return DocumentConfigJpaEntity.builder().documentType(c.getDocumentType().name()).expiryDays(c.getExpiryDays()).warningDays(c.getWarningDays()).build();
    }

    private DocumentConfig toDomain(DocumentConfigJpaEntity e) {
        return DocumentConfig.builder().documentType(DocumentType.valueOf(e.getDocumentType())).expiryDays(e.getExpiryDays()).warningDays(e.getWarningDays()).build();
    }
}
