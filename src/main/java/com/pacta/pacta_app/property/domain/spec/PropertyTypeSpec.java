package com.pacta.pacta_app.property.domain.spec;

import com.pacta.pacta_app.property.domain.Property;

import java.util.List;

public interface PropertyTypeSpec {

    boolean isReadyToPublish(Property p);

    /** Returns human-readable names of fields that are still missing. */
    List<String> missingFields(Property p);
}
