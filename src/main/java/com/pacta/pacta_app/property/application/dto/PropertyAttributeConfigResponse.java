package com.pacta.pacta_app.property.application.dto;

import com.pacta.pacta_app.property.domain.PropertyAttributeConfig;

public record PropertyAttributeConfigResponse(
        String  id,
        String  propertyType,
        String  category,
        String  displayName,
        boolean enabled,
        String  createdBy,
        String  createdAt
) {
    public static PropertyAttributeConfigResponse from(PropertyAttributeConfig c) {
        return new PropertyAttributeConfigResponse(
                c.getId(), c.getPropertyType(), c.getCategory(),
                c.getDisplayName(), c.isEnabled(), c.getCreatedBy(), c.getCreatedAt());
    }
}
