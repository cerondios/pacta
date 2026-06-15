package com.pacta.pacta_app.property.application.dto;

import com.pacta.pacta_app.property.domain.PropertyPurpose;
import com.pacta.pacta_app.property.domain.PropertyType;
import jakarta.validation.constraints.Positive;

import java.util.List;
import java.util.Set;

public record UpdatePropertyRequest(
        PropertyPurpose purpose,
        String          title,
        String          country,
        String          city,
        String       neighborhood,
        String       address,
        PropertyType type,
        Double       area,
        String       areaUnit,
        Integer      bedrooms,
        Integer      bathrooms,
        Integer      floors,
        Integer      parkingSpots,
        Set<String>  amenities,
        List<String> photoKeys,
        String       description,
        @Positive Long monthlyRent,
        String       currency,
        Long         adminFee,
        Integer      minContractMonths,
        Boolean      allowsPets,
        Boolean      allowsSmokers,
        Boolean      allowsChildren
) {}
