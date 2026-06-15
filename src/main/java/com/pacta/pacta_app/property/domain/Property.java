package com.pacta.pacta_app.property.domain;

import com.pacta.pacta_app.property.domain.spec.PropertyTypeSpecFactory;
import com.pacta.pacta_app.shared.domain.DateUtil;
import com.pacta.pacta_app.shared.domain.IdGenerator;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Set;
import java.util.HashSet;

@Getter
@Builder(toBuilder = true)
public class Property {

    private final String         id;
    private final String         landlordId;
    private final String         bankAccountId;

    // ── Location ──────────────────────────────────────────────────────────────
    private final String         country;
    private final String         city;
    private final String         neighborhood;
    private final String         address;

    // ── Identity ──────────────────────────────────────────────────────────────
    private final String         title;

    // ── Purpose & type ───────────────────────────────────────────────────────
    private final PropertyPurpose purpose;   // RENT | SALE — defaults to RENT
    private final PropertyType    type;
    private final Double         area;
    private final String         areaUnit;   // "M2" | "FT2"
    private final Integer        bedrooms;
    private final Integer        bathrooms;
    private final Integer        floors;     // number of floors (stories) in the unit
    private final Integer        parkingSpots;
    private final Set<String>    amenities;  // id from PropertyAttributeConfig
    private final List<String>   photos;
    private final String         description;

    // ── Financial ────────────────────────────────────────────────────────────
    private final Long           monthlyRent;   // in minor units of `currency`
    private final String         currency;      // ISO 4217: "COP" | "USD" | "EUR"
    private final Long           adminFee;      // same currency, minor units

    // ── Contract terms ───────────────────────────────────────────────────────
    private final Integer        minContractMonths;
    private final boolean        allowsPets;
    private final boolean        allowsSmokers;
    private final boolean        allowsChildren;

    // ── Lifecycle ─────────────────────────────────────────────────────────────
    private final PropertyStatus status;
    private final String         createdAt;
    private final String         updatedAt;
    private final String         updatedBy;

    // ── Factory ───────────────────────────────────────────────────────────────

    public static Property create(IdGenerator ids, String landlordId) {
        return Property.builder()
                .id(ids.generate())
                .landlordId(landlordId)
                .purpose(PropertyPurpose.RENT)
                .status(PropertyStatus.DRAFT)
                .amenities(new HashSet<>())
                .photos(List.of())
                .createdAt(DateUtil.now())
                .build();
    }

    // ── Queries ───────────────────────────────────────────────────────────────

    public boolean isDraft()     { return status == PropertyStatus.DRAFT; }
    public boolean isPublished() { return status == PropertyStatus.PUBLISHED; }
    public boolean isBlocked()   { return status == PropertyStatus.BLOCKED; }

    public boolean isReadyToPublish() {
        if (type == null) return false;
        return PropertyTypeSpecFactory.forType(type).isReadyToPublish(this);
    }

    public List<String> missingPublishFields() {
        if (type == null) return List.of("type");
        return PropertyTypeSpecFactory.forType(type).missingFields(this);
    }

    // ── Domain behavior ───────────────────────────────────────────────────────

    public Property submit() {
        if (!isReadyToPublish()) {
            throw new IllegalStateException(
                "Property is incomplete — missing: " + missingPublishFields());
        }
        if (status != PropertyStatus.DRAFT)
            throw new IllegalStateException("Only DRAFT properties can be submitted, current: " + status);
        return toBuilder().status(PropertyStatus.PENDING_REVIEW).updatedAt(DateUtil.now()).build();
    }

    public Property approve(String approvedBy) {
        if (status != PropertyStatus.PENDING_REVIEW)
            throw new IllegalStateException("Can only approve PENDING_REVIEW, current: " + status);
        return toBuilder().status(PropertyStatus.PUBLISHED).updatedAt(DateUtil.now()).updatedBy(approvedBy).build();
    }

    public Property reject(String rejectedBy) {
        if (status != PropertyStatus.PENDING_REVIEW)
            throw new IllegalStateException("Can only reject PENDING_REVIEW, current: " + status);
        return toBuilder().status(PropertyStatus.DRAFT).updatedAt(DateUtil.now()).updatedBy(rejectedBy).build();
    }

    public Property pause() {
        if (status != PropertyStatus.PUBLISHED)
            throw new IllegalStateException("Only PUBLISHED properties can be paused, current: " + status);
        return toBuilder().status(PropertyStatus.PAUSED).updatedAt(DateUtil.now()).build();
    }

    public Property resume() {
        if (status != PropertyStatus.PAUSED)
            throw new IllegalStateException("Only PAUSED properties can be resumed, current: " + status);
        return toBuilder().status(PropertyStatus.PUBLISHED).updatedAt(DateUtil.now()).build();
    }

    /** A landlord accepted a tenant's request — the property is held while the contract gets signed. */
    public Property reserve() {
        if (status != PropertyStatus.PUBLISHED)
            throw new IllegalStateException("Only PUBLISHED properties can be reserved, current: " + status);
        return toBuilder().status(PropertyStatus.RESERVED).updatedAt(DateUtil.now()).build();
    }

    public Property rent() {
        if (status != PropertyStatus.RESERVED)
            throw new IllegalStateException("Only RESERVED properties can be rented, current: " + status);
        return toBuilder().status(PropertyStatus.RENTED).updatedAt(DateUtil.now()).build();
    }

    public Property vacate() {
        if (status != PropertyStatus.RENTED)
            throw new IllegalStateException("Only RENTED properties can be vacated, current: " + status);
        return toBuilder().status(PropertyStatus.PUBLISHED).updatedAt(DateUtil.now()).build();
    }

    public Property block(String blockedBy) {
        if (status == PropertyStatus.BLOCKED)
            throw new IllegalStateException("Property is already blocked");
        if (status == PropertyStatus.ARCHIVED || status == PropertyStatus.DRAFT)
            throw new IllegalStateException("Cannot block a property with status: " + status);
        return toBuilder()
                .status(PropertyStatus.BLOCKED)
                .updatedAt(DateUtil.now())
                .updatedBy(blockedBy)
                .build();
    }

    public Property unblock(String unblockedBy) {
        if (status != PropertyStatus.BLOCKED)
            throw new IllegalStateException("Only BLOCKED properties can be unblocked, current: " + status);
        return toBuilder()
                .status(PropertyStatus.PUBLISHED)
                .updatedAt(DateUtil.now())
                .updatedBy(unblockedBy)
                .build();
    }

    public Property archive() {
        if (status == PropertyStatus.ARCHIVED)
            throw new IllegalStateException("Property is already archived");
        return toBuilder().status(PropertyStatus.ARCHIVED).updatedAt(DateUtil.now()).build();
    }

    public Property restore() {
        if (status != PropertyStatus.ARCHIVED)
            throw new IllegalStateException("Only ARCHIVED properties can be restored, current: " + status);
        return toBuilder().status(PropertyStatus.PENDING_REVIEW).updatedAt(DateUtil.now()).build();
    }
}
