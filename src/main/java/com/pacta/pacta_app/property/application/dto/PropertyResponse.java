package com.pacta.pacta_app.property.application.dto;

import com.pacta.pacta_app.property.domain.Property;
import com.pacta.pacta_app.property.domain.PropertyPurpose;
import com.pacta.pacta_app.shared.domain.StorageService;

import java.util.List;
import java.util.Set;

public record PropertyResponse(
        String       id,
        String       landlordId,
        String       purpose,
        String       status,
        String       title,
        String       country,
        String       city,
        String       neighborhood,
        String       address,
        String       type,
        Double       area,
        String       areaUnit,
        Integer      bedrooms,
        Integer      bathrooms,
        Integer      floors,
        Integer      parkingSpots,
        Set<String>  amenities,
        List<String> photoKeys,
        List<String> photoUrls,
        String       description,
        Long         monthlyRent,
        String       currency,
        Long         adminFee,
        Integer      minContractMonths,
        boolean      allowsPets,
        boolean      allowsSmokers,
        boolean      allowsChildren,
        List<String> missingPublishFields,
        String       createdAt,
        String       updatedAt
) {
    public static PropertyResponse from(Property p, StorageService storage) {
        List<String> keys = p.getPhotos() != null ? p.getPhotos() : List.of();
        List<String> urls = keys.stream().map(storage::getViewUrl).toList();
        return new PropertyResponse(
                p.getId(),
                p.getLandlordId(),
                p.getPurpose() != null ? p.getPurpose().name() : PropertyPurpose.RENT.name(),
                p.getStatus().name(),
                p.getTitle(),
                p.getCountry(),
                p.getCity(),
                p.getNeighborhood(),
                p.getAddress(),
                p.getType() != null ? p.getType().name() : null,
                p.getArea(),
                p.getAreaUnit(),
                p.getBedrooms(),
                p.getBathrooms(),
                p.getFloors(),
                p.getParkingSpots(),
                p.getAmenities() != null ? p.getAmenities() : Set.of(),
                keys,
                urls,
                p.getDescription(),
                p.getMonthlyRent(),
                p.getCurrency(),
                p.getAdminFee(),
                p.getMinContractMonths(),
                p.isAllowsPets(),
                p.isAllowsSmokers(),
                p.isAllowsChildren(),
                p.missingPublishFields(),
                p.getCreatedAt(),
                p.getUpdatedAt()
        );
    }
}
