package com.pacta.pacta_app.property.infrastructure.persistence;

import com.pacta.pacta_app.property.application.dto.PropertySearchRequest;
import com.pacta.pacta_app.property.domain.IPropertyRepository;
import com.pacta.pacta_app.property.domain.Property;
import com.pacta.pacta_app.property.domain.PropertyStatus;
import com.pacta.pacta_app.property.domain.PropertyPurpose;
import com.pacta.pacta_app.property.domain.PropertyType;
import com.pacta.pacta_app.shared.domain.PagedResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@Profile({"dev", "prod"})
@RequiredArgsConstructor
public class PostgresPropertyRepository implements IPropertyRepository {

    private static final String SEP = "|";

    private final PropertySpringRepository jpa;

    @Override public Property save(Property p)              { jpa.save(toEntity(p)); return p; }
    @Override public Optional<Property> findById(String id) { return jpa.findById(id).map(this::toDomain); }

    @Override
    public List<Property> findByLandlordId(String landlordId) {
        return jpa.findByLandlordId(landlordId).stream().map(this::toDomain).toList();
    }

    @Override
    public List<Property> findByLandlordIdExcludingStatus(String landlordId, PropertyStatus excluded) {
        return jpa.findByLandlordIdAndStatusNot(landlordId, excluded.name()).stream().map(this::toDomain).toList();
    }

    @Override
    public List<Property> findByStatus(PropertyStatus status) {
        return jpa.findByStatus(status.name()).stream().map(this::toDomain).toList();
    }

    @Override
    public PagedResult<Property> search(PropertySearchRequest req) {
        String sortBy   = req.sortBy()   != null ? req.sortBy()   : "created_at";
        String sortType = req.sortType() != null ? req.sortType().toUpperCase() : "DESC";
        Page<PropertyJpaEntity> page = jpa.searchFiltered(
                PageRequest.of(req.page(), req.size()),
                req.excludeLandlordId(),
                req.city(), req.neighborhood(), req.country(), req.type(), req.currency(),
                req.minRent(), req.maxRent(), req.minArea(), req.maxArea(),
                req.minBedrooms(), req.minBathrooms(), req.minFloors(), req.minParkingSpots(),
                req.allowsPets(), req.allowsSmokers(), req.allowsChildren(),
                req.minContractMonths(), req.maxContractMonths(),
                req.amenity(), sortBy, sortType
        );
        return new PagedResult<>(
                page.getContent().stream().map(this::toDomain).toList(),
                req.page(), req.size(), page.getTotalElements(), page.getTotalPages()
        );
    }

    @Override
    public List<Property> findAll() {
        return jpa.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public void deleteById(String id) {
        jpa.deleteById(id);
    }

    private PropertyJpaEntity toEntity(Property p) {
        return PropertyJpaEntity.builder()
                .id(p.getId())
                .landlordId(p.getLandlordId())
                .purpose(p.getPurpose() != null ? p.getPurpose().name() : PropertyPurpose.RENT.name())
                .title(p.getTitle())
                .country(p.getCountry())
                .city(p.getCity())
                .neighborhood(p.getNeighborhood())
                .address(p.getAddress())
                .type(p.getType() != null ? p.getType().name() : null)
                .area(p.getArea())
                .areaUnit(p.getAreaUnit())
                .bedrooms(p.getBedrooms())
                .bathrooms(p.getBathrooms())
                .floors(p.getFloors())
                .parkingSpots(p.getParkingSpots())
                .amenities(serializeAmenities(p.getAmenities()))
                .photoKeys(serializeList(p.getPhotos()))
                .description(p.getDescription())
                .monthlyRent(p.getMonthlyRent())
                .currency(p.getCurrency())
                .adminFee(p.getAdminFee())
                .minContractMonths(p.getMinContractMonths())
                .allowsPets(p.isAllowsPets())
                .allowsSmokers(p.isAllowsSmokers())
                .allowsChildren(p.isAllowsChildren())
                .status(p.getStatus().name())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .updatedBy(p.getUpdatedBy())
                .build();
    }

    private Property toDomain(PropertyJpaEntity e) {
        return Property.builder()
                .id(e.getId())
                .landlordId(e.getLandlordId())
                .purpose(e.getPurpose() != null ? PropertyPurpose.valueOf(e.getPurpose()) : PropertyPurpose.RENT)
                .title(e.getTitle())
                .country(e.getCountry())
                .city(e.getCity())
                .neighborhood(e.getNeighborhood())
                .address(e.getAddress())
                .type(e.getType() != null ? PropertyType.valueOf(e.getType()) : null)
                .area(e.getArea())
                .areaUnit(e.getAreaUnit())
                .bedrooms(e.getBedrooms())
                .bathrooms(e.getBathrooms())
                .floors(e.getFloors())
                .parkingSpots(e.getParkingSpots())
                .amenities(deserializeAmenities(e.getAmenities()))
                .photos(deserializeList(e.getPhotoKeys()))
                .description(e.getDescription())
                .monthlyRent(e.getMonthlyRent())
                .currency(e.getCurrency())
                .adminFee(e.getAdminFee())
                .minContractMonths(e.getMinContractMonths())
                .allowsPets(e.isAllowsPets())
                .allowsSmokers(e.isAllowsSmokers())
                .allowsChildren(e.isAllowsChildren())
                .status(PropertyStatus.valueOf(e.getStatus()))
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .updatedBy(e.getUpdatedBy())
                .build();
    }

    private static String serializeAmenities(Set<String> amenities) {
        if (amenities == null || amenities.isEmpty()) return "";
        return String.join(SEP, amenities);
    }

    private static Set<String> deserializeAmenities(String raw) {
        if (raw == null || raw.isBlank()) return new HashSet<>();
        return new HashSet<>(Arrays.asList(raw.split("\\|")));
    }

    private static String serializeList(List<String> list) {
        if (list == null || list.isEmpty()) return "";
        return String.join(SEP, list);
    }

    private static List<String> deserializeList(String raw) {
        if (raw == null || raw.isBlank()) return new ArrayList<>();
        return new ArrayList<>(Arrays.asList(raw.split("\\|")));
    }
}
