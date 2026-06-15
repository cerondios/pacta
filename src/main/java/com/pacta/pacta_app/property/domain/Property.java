package com.pacta.pacta_app.property.domain;

import com.pacta.pacta_app.shared.domain.DateUtil;
import com.pacta.pacta_app.shared.domain.IdGenerator;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Set;

@Getter
@Builder(toBuilder = true)
public class Property {

    private final String         id;
    private final String         landlordId;
    private final String         bankAccountId;

    // ── Location ─────────────────────────────────────────────────────────────
    private final String         city;

    // ── Physical details ─────────────────────────────────────────────────────
    private final PropertyType   type;
    private final Integer        areaM2;
    private final Integer        bedrooms;
    private final Integer        bathrooms;
    private final Integer        floor;
    private final Integer        stratum;
    private final Integer        parkingSpots;
    private final Set<Amenity>   amenities;
    private final List<String>   photos;
    private final String         description;

    // ── Financial ────────────────────────────────────────────────────────────
    private final Long           monthlyRentCents;
    private final Long           adminFeeCents;

    // ── Contract terms ───────────────────────────────────────────────────────
    private final Integer        minContractMonths;
    private final boolean        allowsPets;
    private final boolean        allowsSmokers;
    private final boolean        allowsChildren;

    // ── Lifecycle ─────────────────────────────────────────────────────────────
    private final PropertyStatus status;
    private final PropertyStatus previousStatus;
    private final String         createdAt;
    private final String         updatedAt;
    private final String         updatedBy;

    // ── Factory ───────────────────────────────────────────────────────────────

    public static Property create(IdGenerator ids, String landlordId) {
        return Property.builder()
                .id(ids.generate())
                .landlordId(landlordId)
                .status(PropertyStatus.DRAFT)
                .amenities(Set.of())
                .photos(List.of())
                .createdAt(DateUtil.now())
                .build();
    }

    // ── Queries ───────────────────────────────────────────────────────────────

    public boolean isDraft()     { return status == PropertyStatus.DRAFT; }
    public boolean isPublished() { return status == PropertyStatus.PUBLISHED; }
    public boolean isBlocked()   { return status == PropertyStatus.BLOCKED; }

    public boolean isReadyToPublish() {
        return bankAccountId != null
            && photos != null && photos.size() >= 4
            && type != null
            && city != null && !city.isBlank()
            && areaM2 != null && areaM2 > 0;
    }

    // ── Domain behavior ───────────────────────────────────────────────────────

    /** Landlord submits the draft for reviewer approval. */
    public Property submit() {
        if (!isReadyToPublish())
            throw new IllegalStateException("Property is incomplete — cannot submit for review");
        if (status != PropertyStatus.DRAFT)
            throw new IllegalStateException("Only DRAFT properties can be submitted, current: " + status);
        return toBuilder().status(PropertyStatus.PENDING_REVIEW).updatedAt(DateUtil.now()).build();
    }

    /** Reviewer approves → live. */
    public Property approve(String approvedBy) {
        if (status != PropertyStatus.PENDING_REVIEW)
            throw new IllegalStateException("Can only approve PENDING_REVIEW, current: " + status);
        return toBuilder().status(PropertyStatus.PUBLISHED).updatedAt(DateUtil.now()).updatedBy(approvedBy).build();
    }

    /** Reviewer rejects → back to DRAFT so landlord can fix. */
    public Property reject(String rejectedBy) {
        if (status != PropertyStatus.PENDING_REVIEW)
            throw new IllegalStateException("Can only reject PENDING_REVIEW, current: " + status);
        return toBuilder().status(PropertyStatus.DRAFT).updatedAt(DateUtil.now()).updatedBy(rejectedBy).build();
    }

    /** Landlord temporarily hides a live listing. */
    public Property pause() {
        if (status != PropertyStatus.PUBLISHED)
            throw new IllegalStateException("Only PUBLISHED properties can be paused, current: " + status);
        return toBuilder().status(PropertyStatus.PAUSED).updatedAt(DateUtil.now()).build();
    }

    /** Landlord re-exposes a paused listing. */
    public Property resume() {
        if (status != PropertyStatus.PAUSED)
            throw new IllegalStateException("Only PAUSED properties can be resumed, current: " + status);
        return toBuilder().status(PropertyStatus.PUBLISHED).updatedAt(DateUtil.now()).build();
    }

    /** A lease starts — remove from available stock. */
    public Property rent() {
        if (status != PropertyStatus.PUBLISHED)
            throw new IllegalStateException("Only PUBLISHED properties can be rented, current: " + status);
        return toBuilder().status(PropertyStatus.RENTED).updatedAt(DateUtil.now()).build();
    }

    /** Lease ends — back on the market. */
    public Property vacate() {
        if (status != PropertyStatus.RENTED)
            throw new IllegalStateException("Only RENTED properties can be vacated, current: " + status);
        return toBuilder().status(PropertyStatus.PUBLISHED).updatedAt(DateUtil.now()).build();
    }

    /** Admin blocks a visible property — stores previous status for restore. */
    public Property block(String blockedBy) {
        if (status == PropertyStatus.BLOCKED)
            throw new IllegalStateException("Property is already blocked");
        if (status == PropertyStatus.ARCHIVED || status == PropertyStatus.DRAFT)
            throw new IllegalStateException("Cannot block a property with status: " + status);
        return toBuilder()
                .previousStatus(status)
                .status(PropertyStatus.BLOCKED)
                .updatedAt(DateUtil.now())
                .updatedBy(blockedBy)
                .build();
    }

    /** Admin restores a blocked property to its previous status. */
    public Property unblock(String unblockedBy) {
        if (status != PropertyStatus.BLOCKED)
            throw new IllegalStateException("Only BLOCKED properties can be unblocked, current: " + status);
        PropertyStatus restored = previousStatus != null ? previousStatus : PropertyStatus.DRAFT;
        return toBuilder()
                .status(restored)
                .previousStatus(null)
                .updatedAt(DateUtil.now())
                .updatedBy(unblockedBy)
                .build();
    }

    /** Permanently delist. */
    public Property archive() {
        if (status == PropertyStatus.ARCHIVED)
            throw new IllegalStateException("Property is already archived");
        return toBuilder().status(PropertyStatus.ARCHIVED).updatedAt(DateUtil.now()).build();
    }
}
