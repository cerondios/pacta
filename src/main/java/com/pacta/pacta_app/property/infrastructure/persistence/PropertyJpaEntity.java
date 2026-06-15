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

    private String  purpose;
    private String  title;
    private String  country;
    private String  city;
    private String  neighborhood;
    private String  address;
    private String  type;
    private Double  area;
    private String  areaUnit;
    private Integer bedrooms;
    private Integer bathrooms;
    private Integer floors;
    private Integer parkingSpots;

    @Column(columnDefinition = "TEXT")
    private String amenities;   // pipe-separated Amenity names

    @Column(columnDefinition = "TEXT")
    private String photoKeys;   // pipe-separated S3 keys

    @Column(columnDefinition = "TEXT")
    private String description;

    private Long   monthlyRent;
    private String currency;
    private Long   adminFee;

    private Integer minContractMonths;

    @Column(nullable = false)
    private boolean allowsPets;

    @Column(nullable = false)
    private boolean allowsSmokers;

    @Column(nullable = false)
    private boolean allowsChildren;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private String createdAt;

    private String updatedAt;
    private String updatedBy;
}
