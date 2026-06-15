package com.pacta.pacta_app.property.application.dto;

import com.pacta.pacta_app.property.domain.Amenity;
import com.pacta.pacta_app.property.domain.PropertyType;

import java.util.List;
import java.util.Set;

public record UpdatePropertyRequest(
        String       city,
        String       bankAccountId,
        PropertyType type,
        Integer      areaM2,
        Integer      bedrooms,
        Integer      bathrooms,
        Integer      floor,
        Integer      stratum,
        Integer      parkingSpots,
        Set<Amenity> amenities,
        List<String> photoUrls,
        String       description,
        Long         monthlyRentCents,
        Long         adminFeeCents,
        Integer      minContractMonths,
        Boolean      allowsPets,
        Boolean      allowsSmokers,
        Boolean      allowsChildren
) {}
