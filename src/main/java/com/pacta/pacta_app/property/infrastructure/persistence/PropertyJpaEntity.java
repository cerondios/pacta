package com.pacta.pacta_app.property.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "properties")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PropertyJpaEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String landlordId;

    private String  bankAccountId;
    private String  city;
    private String  type;
    private Integer areaM2;
    private Integer bedrooms;
    private Integer bathrooms;
    private Integer floor;
    private Integer stratum;
    private Integer parkingSpots;

    @Column(columnDefinition = "TEXT")
    private String amenities;  // pipe-separated Amenity names

    @Column(columnDefinition = "TEXT")
    private String photoUrls;  // pipe-separated URLs

    @Column(columnDefinition = "TEXT")
    private String description;

    private Long    monthlyRentCents;
    private Long    adminFeeCents;
    private Integer minContractMonths;

    @Column(nullable = false)
    private boolean allowsPets;

    @Column(nullable = false)
    private boolean allowsSmokers;

    @Column(nullable = false)
    private boolean allowsChildren;

    @Column(nullable = false)
    private String status;

    private String previousStatus;

    @Column(nullable = false)
    private String createdAt;

    private String updatedAt;
    private String updatedBy;
}
