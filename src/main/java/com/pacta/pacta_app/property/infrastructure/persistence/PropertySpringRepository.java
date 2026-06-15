package com.pacta.pacta_app.property.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

interface PropertySpringRepository extends JpaRepository<PropertyJpaEntity, String> {
    List<PropertyJpaEntity> findByLandlordId(String landlordId);
    List<PropertyJpaEntity> findByLandlordIdAndStatusNot(String landlordId, String status);
    List<PropertyJpaEntity> findByStatus(String status);

    @Query(
        value = """
            SELECT * FROM properties
            WHERE status = 'PUBLISHED'
              AND (:excludeLandlordId IS NULL OR landlord_id <> :excludeLandlordId)
              AND (:city              IS NULL OR LOWER(city)          LIKE LOWER(CONCAT('%', :city, '%')))
              AND (:neighborhood      IS NULL OR LOWER(neighborhood)  LIKE LOWER(CONCAT('%', :neighborhood, '%')))
              AND (:country           IS NULL OR LOWER(country)       = LOWER(:country))
              AND (:type              IS NULL OR UPPER(type)          = UPPER(:type))
              AND (:currency          IS NULL OR LOWER(currency)      = LOWER(:currency))
              AND (:minRent           IS NULL OR monthly_rent        >= :minRent)
              AND (:maxRent           IS NULL OR monthly_rent        <= :maxRent)
              AND (:minArea           IS NULL OR area                >= :minArea)
              AND (:maxArea           IS NULL OR area                <= :maxArea)
              AND (:minBedrooms       IS NULL OR bedrooms            >= :minBedrooms)
              AND (:minBathrooms      IS NULL OR bathrooms           >= :minBathrooms)
              AND (:minFloors         IS NULL OR floors              >= :minFloors)
              AND (:minParkingSpots   IS NULL OR parking_spots       >= :minParkingSpots)
              AND (:allowsPets        IS NULL OR allows_pets         = :allowsPets)
              AND (:allowsSmokers     IS NULL OR allows_smokers      = :allowsSmokers)
              AND (:allowsChildren    IS NULL OR allows_children     = :allowsChildren)
              AND (:minContractMonths IS NULL OR min_contract_months >= :minContractMonths)
              AND (:maxContractMonths IS NULL OR min_contract_months <= :maxContractMonths)
              AND (:amenity           IS NULL OR amenities LIKE CONCAT('%', :amenity, '%'))
            ORDER BY
              CASE WHEN :sortBy = 'monthly_rent' AND :sortType = 'ASC'  THEN monthly_rent  END ASC  NULLS LAST,
              CASE WHEN :sortBy = 'monthly_rent' AND :sortType = 'DESC' THEN monthly_rent  END DESC NULLS LAST,
              CASE WHEN :sortBy = 'area'         AND :sortType = 'ASC'  THEN area          END ASC  NULLS LAST,
              CASE WHEN :sortBy = 'area'         AND :sortType = 'DESC' THEN area          END DESC NULLS LAST,
              CASE WHEN :sortBy = 'bedrooms'     AND :sortType = 'ASC'  THEN bedrooms      END ASC  NULLS LAST,
              CASE WHEN :sortBy = 'bedrooms'     AND :sortType = 'DESC' THEN bedrooms      END DESC NULLS LAST,
              created_at DESC
            """,
        countQuery = """
            SELECT COUNT(*) FROM properties
            WHERE status = 'PUBLISHED'
              AND (:excludeLandlordId IS NULL OR landlord_id <> :excludeLandlordId)
              AND (:city              IS NULL OR LOWER(city)          LIKE LOWER(CONCAT('%', :city, '%')))
              AND (:neighborhood      IS NULL OR LOWER(neighborhood)  LIKE LOWER(CONCAT('%', :neighborhood, '%')))
              AND (:country           IS NULL OR LOWER(country)       = LOWER(:country))
              AND (:type              IS NULL OR UPPER(type)          = UPPER(:type))
              AND (:currency          IS NULL OR LOWER(currency)      = LOWER(:currency))
              AND (:minRent           IS NULL OR monthly_rent        >= :minRent)
              AND (:maxRent           IS NULL OR monthly_rent        <= :maxRent)
              AND (:minArea           IS NULL OR area                >= :minArea)
              AND (:maxArea           IS NULL OR area                <= :maxArea)
              AND (:minBedrooms       IS NULL OR bedrooms            >= :minBedrooms)
              AND (:minBathrooms      IS NULL OR bathrooms           >= :minBathrooms)
              AND (:minFloors         IS NULL OR floors              >= :minFloors)
              AND (:minParkingSpots   IS NULL OR parking_spots       >= :minParkingSpots)
              AND (:allowsPets        IS NULL OR allows_pets         = :allowsPets)
              AND (:allowsSmokers     IS NULL OR allows_smokers      = :allowsSmokers)
              AND (:allowsChildren    IS NULL OR allows_children     = :allowsChildren)
              AND (:minContractMonths IS NULL OR min_contract_months >= :minContractMonths)
              AND (:maxContractMonths IS NULL OR min_contract_months <= :maxContractMonths)
              AND (:amenity           IS NULL OR amenities LIKE CONCAT('%', :amenity, '%'))
            """,
        nativeQuery = true
    )
    Page<PropertyJpaEntity> searchFiltered(
            Pageable                            pageable,
            @Param("excludeLandlordId") String  excludeLandlordId,
            @Param("city")              String  city,
            @Param("neighborhood")      String  neighborhood,
            @Param("country")           String  country,
            @Param("type")              String  type,
            @Param("currency")          String  currency,
            @Param("minRent")           Long    minRent,
            @Param("maxRent")           Long    maxRent,
            @Param("minArea")           Integer minArea,
            @Param("maxArea")           Integer maxArea,
            @Param("minBedrooms")       Integer minBedrooms,
            @Param("minBathrooms")      Integer minBathrooms,
            @Param("minFloors")         Integer minFloors,
            @Param("minParkingSpots")   Integer minParkingSpots,
            @Param("allowsPets")        Boolean allowsPets,
            @Param("allowsSmokers")     Boolean allowsSmokers,
            @Param("allowsChildren")    Boolean allowsChildren,
            @Param("minContractMonths") Integer minContractMonths,
            @Param("maxContractMonths") Integer maxContractMonths,
            @Param("amenity")           String  amenity,
            @Param("sortBy")            String  sortBy,
            @Param("sortType")          String  sortType
    );
}
