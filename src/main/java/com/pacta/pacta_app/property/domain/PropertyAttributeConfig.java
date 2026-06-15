package com.pacta.pacta_app.property.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class PropertyAttributeConfig {

    private final String  id;
    private final String  propertyType;  // "APARTMENT" | "HOUSE" | "PARKING" | "ALL"
    private final String  category;      // "AMENITY" | "SERVICE" | "FEATURE"
    private final String  displayName;   // shown in UI, e.g. "Gimnasio"
    private final boolean enabled;
    private final String  createdBy;
    private final String  createdAt;
}
