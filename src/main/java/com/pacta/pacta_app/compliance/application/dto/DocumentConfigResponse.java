package com.pacta.pacta_app.compliance.application.dto;

import com.pacta.pacta_app.compliance.domain.DocumentConfig;

public record DocumentConfigResponse(String documentType, int expiryDays, int warningDays) {
    public static DocumentConfigResponse from(DocumentConfig c) {
        return new DocumentConfigResponse(c.getDocumentType().name(), c.getExpiryDays(), c.getWarningDays());
    }
}
