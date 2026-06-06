package com.pacta.pacta_app.compliance.application;

import com.pacta.pacta_app.compliance.domain.DocumentConfig;
import com.pacta.pacta_app.compliance.domain.IDocumentConfigRepository;
import com.pacta.pacta_app.compliance.domain.DocumentType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentConfigService {

    private final IDocumentConfigRepository docConfigs;

    public List<DocumentConfig> findAll() {
        return docConfigs.findAll();
    }

    public DocumentConfig update(DocumentType type, int expiryDays, int warningDays) {
        return docConfigs.save(DocumentConfig.builder()
                .documentType(type).expiryDays(expiryDays).warningDays(warningDays)
                .build());
    }
}
