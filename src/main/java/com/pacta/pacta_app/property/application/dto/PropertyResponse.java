package com.pacta.pacta_app.property.application.dto;

import com.pacta.pacta_app.property.domain.Amenity;
import com.pacta.pacta_app.property.domain.Property;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public record PropertyResponse(
        String       id,
        String       landlordId,
        String       bankAccountId,
        String       status,
        String       city,
        String       type,
        Integer      areaM2,
        Integer      bedrooms,
        Integer      bathrooms,
        Integer      floor,
        Integer      stratum,
        Integer      parkingSpots,
        Set<String>  amenities,
        List<String> photoUrls,
        String       description,
        Long         monthlyRentCents,
        Long         adminFeeCents,
        Integer      minContractMonths,
        boolean      allowsPets,
        boolean      allowsSmokers,
        boolean      allowsChildren,
        String       createdAt,
        String       updatedAt
) {
    public static PropertyResponse from(Property p) {
        return new PropertyResponse(
                p.getId(),
                p.getLandlordId(),
                p.getBankAccountId(),
                p.getStatus().name(),
                p.getCity(),
                p.getType() != null ? p.getType().name() : null,
                p.getAreaM2(),
                p.getBedrooms(),
                p.getBathrooms(),
                p.getFloor(),
                p.getStratum(),
                p.getParkingSpots(),
                p.getAmenities() != null
                        ? p.getAmenities().stream().map(Amenity::name).collect(Collectors.toSet())
                        : Set.of(),
                p.getPhotos() != null ? p.getPhotos() : List.of(),
                p.getDescription(),
                p.getMonthlyRentCents(),
                p.getAdminFeeCents(),
                p.getMinContractMonths(),
                p.isAllowsPets(),
                p.isAllowsSmokers(),
                p.isAllowsChildren(),
                p.getCreatedAt(),
                p.getUpdatedAt()
        );
    }
}
