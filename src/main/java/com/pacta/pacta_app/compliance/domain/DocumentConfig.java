package com.pacta.pacta_app.compliance.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DocumentConfig {
    private final DocumentType documentType;
    private final int          expiryDays;
    private final int          warningDays;
}
