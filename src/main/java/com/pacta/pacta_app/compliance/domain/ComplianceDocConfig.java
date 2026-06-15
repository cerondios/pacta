package com.pacta.pacta_app.compliance.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class ComplianceDocConfig {
    private final String id;
    private final String countryCode;
    private final String typeCode;
    private final String displayName;
}
