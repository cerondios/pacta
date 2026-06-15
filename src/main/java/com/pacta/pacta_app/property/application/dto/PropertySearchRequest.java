package com.pacta.pacta_app.property.application.dto;

/**
 * All fields are optional — null means "no filter".
 * sortBy: DB column name (monthly_rent | area | bedrooms | created_at). Default: created_at.
 * sortType: ASC | DESC. Default: DESC.
 * excludeLandlordId: omit properties owned by this user (the caller's own listings).
 */
public record PropertySearchRequest(
        String  city,
        String  neighborhood,
        String  country,
        String  type,
        String  currency,
        Long    minRent,
        Long    maxRent,
        Integer minArea,
        Integer maxArea,
        Integer minBedrooms,
        Integer minBathrooms,
        Integer minFloors,
        Integer minParkingSpots,
        Boolean allowsPets,
        Boolean allowsSmokers,
        Boolean allowsChildren,
        Integer minContractMonths,
        Integer maxContractMonths,
        String  amenity,
        String  sortBy,    // monthly_rent | area | bedrooms | created_at
        String  sortType,  // ASC | DESC
        String  excludeLandlordId,
        int     page,      // 0-based
        int     size       // items per page
) {}
