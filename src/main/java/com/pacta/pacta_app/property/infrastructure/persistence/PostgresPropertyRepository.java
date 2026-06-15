package com.pacta.pacta_app.property.infrastructure.persistence;

import com.pacta.pacta_app.property.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

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
    public List<Property> findByStatus(PropertyStatus status) {
        return jpa.findByStatus(status.name()).stream().map(this::toDomain).toList();
    }

    private PropertyJpaEntity toEntity(Property p) {
        return PropertyJpaEntity.builder()
                .id(p.getId())
                .landlordId(p.getLandlordId())
                .bankAccountId(p.getBankAccountId())
                .city(p.getCity())
                .type(p.getType() != null ? p.getType().name() : null)
                .areaM2(p.getAreaM2())
                .bedrooms(p.getBedrooms())
                .bathrooms(p.getBathrooms())
                .floor(p.getFloor())
                .stratum(p.getStratum())
                .parkingSpots(p.getParkingSpots())
                .amenities(serializeAmenities(p.getAmenities()))
                .photoUrls(serializeList(p.getPhotos()))
                .description(p.getDescription())
                .monthlyRentCents(p.getMonthlyRentCents())
                .adminFeeCents(p.getAdminFeeCents())
                .minContractMonths(p.getMinContractMonths())
                .allowsPets(p.isAllowsPets())
                .allowsSmokers(p.isAllowsSmokers())
                .allowsChildren(p.isAllowsChildren())
                .status(p.getStatus().name())
                .previousStatus(p.getPreviousStatus() != null ? p.getPreviousStatus().name() : null)
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .updatedBy(p.getUpdatedBy())
                .build();
    }

    private Property toDomain(PropertyJpaEntity e) {
        return Property.builder()
                .id(e.getId())
                .landlordId(e.getLandlordId())
                .bankAccountId(e.getBankAccountId())
                .city(e.getCity())
                .type(e.getType() != null ? PropertyType.valueOf(e.getType()) : null)
                .areaM2(e.getAreaM2())
                .bedrooms(e.getBedrooms())
                .bathrooms(e.getBathrooms())
                .floor(e.getFloor())
                .stratum(e.getStratum())
                .parkingSpots(e.getParkingSpots())
                .amenities(deserializeAmenities(e.getAmenities()))
                .photos(deserializeList(e.getPhotoUrls()))
                .description(e.getDescription())
                .monthlyRentCents(e.getMonthlyRentCents())
                .adminFeeCents(e.getAdminFeeCents())
                .minContractMonths(e.getMinContractMonths())
                .allowsPets(e.isAllowsPets())
                .allowsSmokers(e.isAllowsSmokers())
                .allowsChildren(e.isAllowsChildren())
                .status(PropertyStatus.valueOf(e.getStatus()))
                .previousStatus(e.getPreviousStatus() != null ? PropertyStatus.valueOf(e.getPreviousStatus()) : null)
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .updatedBy(e.getUpdatedBy())
                .build();
    }

    private static String serializeAmenities(Set<Amenity> amenities) {
        if (amenities == null || amenities.isEmpty()) return "";
        return amenities.stream().map(Enum::name).collect(Collectors.joining(SEP));
    }

    private static Set<Amenity> deserializeAmenities(String raw) {
        if (raw == null || raw.isBlank()) return new HashSet<>();
        return Arrays.stream(raw.split("\\|")).map(Amenity::valueOf).collect(Collectors.toSet());
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
