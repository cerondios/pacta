package com.pacta.pacta_app.user.domain;

import com.pacta.pacta_app.shared.domain.DateUtil;
import com.pacta.pacta_app.shared.domain.IdGenerator;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.HashSet;
import java.util.Set;

@Getter
@Builder(toBuilder = true)
public class User {

    private final String      id;
    private final String      fullName;
    private final String      email;
    private final Phone       phone;
    private final String      country;
    private final String      city;
    @Singular
    private final Set<Role>   roles;
    private final UserStatus  status;
    private final int         score;
    private final String      createdAt;
    private final String      updatedAt;
    private final String      updatedBy;
    private final String      removalReason;
    private final String      removedAt;
    private final String      removedBy;

    // ── Factory ───────────────────────────────────────────────────────────────

    public static User create(IdGenerator ids, String fullName, String email, Phone phone, String country) {
        return User.builder()
                .id(ids.generate())
                .fullName(fullName)
                .email(email)
                .phone(phone)
                .country(country)
                .roles(new HashSet<>())
                .status(UserStatus.PENDING_VERIFICATION)
                .score(0)
                .createdAt(DateUtil.now())
                .build();
    }

    // ── Queries ───────────────────────────────────────────────────────────────

    public boolean hasRole(Role role) {
        return roles != null && roles.contains(role);
    }

    public boolean isPendingVerification() {
        return status == UserStatus.PENDING_VERIFICATION;
    }

    public boolean isCompleted() {
        return status == UserStatus.COMPLETED;
    }

    public boolean isActive() {
        return status == UserStatus.ACTIVE || status == UserStatus.COMPLETED;
    }

    // ── Domain behavior ───────────────────────────────────────────────────────

    /** Email OTP verified → ready for KYC upload. */
    public User verifyEmail() {
        if (status != UserStatus.PENDING_VERIFICATION)
            throw new IllegalStateException("Can only verify email from PENDING_VERIFICATION, current: " + status);
        return toBuilder().status(UserStatus.PENDING_KYC).updatedAt(DateUtil.now()).build();
    }

    /** KYC approved by reviewer. */
    public User approveKyc(String approvedBy) {
        if (status != UserStatus.PENDING_KYC)
            throw new IllegalStateException("Can only approve KYC from PENDING_KYC, current: " + status);
        return toBuilder().status(UserStatus.ACTIVE).updatedAt(DateUtil.now()).updatedBy(approvedBy).build();
    }

    /** KYC rejected — user must re-upload. Status stays PENDING_KYC. */
    public User rejectKyc(String rejectedBy) {
        return toBuilder().updatedAt(DateUtil.now()).updatedBy(rejectedBy).build();
    }

    /** All compliance docs approved + bank account → COMPLETED. */
    public User complete(String updatedBy) {
        if (status != UserStatus.ACTIVE)
            throw new IllegalStateException("Can only complete from ACTIVE, current: " + status);
        return toBuilder().status(UserStatus.COMPLETED).updatedAt(DateUtil.now()).updatedBy(updatedBy).build();
    }

    /** A compliance doc expired — drop from COMPLETED back to ACTIVE. */
    public User downgradeToActive() {
        return toBuilder().status(UserStatus.ACTIVE).updatedAt(DateUtil.now()).build();
    }

    /** Admin blocks the user. */
    public User block(String blockedBy) {
        if (status == UserStatus.BLOCKED)
            throw new IllegalStateException("User is already blocked");
        if (status == UserStatus.REMOVED)
            throw new IllegalStateException("Cannot block a removed user");
        return toBuilder().status(UserStatus.BLOCKED).updatedAt(DateUtil.now()).updatedBy(blockedBy).build();
    }

    /** Admin unblocks — restarts onboarding. */
    public User unblock(String unblockedBy) {
        if (status != UserStatus.BLOCKED)
            throw new IllegalStateException("User is not blocked");
        return toBuilder().status(UserStatus.PENDING_VERIFICATION).updatedAt(DateUtil.now()).updatedBy(unblockedBy).build();
    }

    /** Soft-delete. */
    public User remove(String reason, String removedBy) {
        if (status == UserStatus.REMOVED)
            throw new IllegalStateException("User is already removed");
        return toBuilder()
                .status(UserStatus.REMOVED)
                .removalReason(reason)
                .removedAt(DateUtil.now())
                .removedBy(removedBy)
                .updatedAt(DateUtil.now())
                .updatedBy(removedBy)
                .build();
    }

    /**
     * One-time onboarding completion — sets roles and city.
     * Throws if either has already been set; neither can be changed afterwards.
     */
    public User completeOnboarding(Set<Role> roles, String city) {
        if (this.roles != null && !this.roles.isEmpty()) {
            throw new IllegalStateException("Onboarding already completed: roles are already set");
        }
        if (this.city != null && !this.city.isBlank()) {
            throw new IllegalStateException("Onboarding already completed: city is already set");
        }
        return toBuilder()
                .clearRoles()
                .roles(roles)
                .city(city)
                .updatedAt(DateUtil.now())
                .build();
    }

    /** Update personal profile data. */
    public User updateProfile(String fullName, Phone phone, String updatedBy) {
        return toBuilder()
                .fullName(fullName)
                .phone(phone)
                .updatedAt(DateUtil.now())
                .updatedBy(updatedBy)
                .build();
    }

    /** Adds points to the score. */
    public User addScore(int points) {
        return toBuilder().score(this.score + points).updatedAt(DateUtil.now()).build();
    }
}
