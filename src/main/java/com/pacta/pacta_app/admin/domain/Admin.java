package com.pacta.pacta_app.admin.domain;

import com.pacta.pacta_app.shared.domain.DateUtil;
import com.pacta.pacta_app.shared.domain.IdGenerator;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class Admin {

    private final String      id;
    private final String      fullName;
    private final String      email;
    private final AdminRole   role;
    private final AdminStatus status;
    private final String      passwordHash;
    private final String      createdAt;
    private final String      updatedAt;

    // ── Queries ───────────────────────────────────────────────────────────────

    public boolean isActive() {
        return status == AdminStatus.ACTIVE;
    }

    // ── Capabilities ─────────────────────────────────────────────────────────

    /** Can approve/reject KYC and compliance documents. */
    public boolean canReview() {
        return isActive() && (
                role == AdminRole.REVIEWER ||
                role == AdminRole.SUPER_ADMIN);
    }

    /** Can block, unblock and remove platform users. */
    public boolean canManageUsers() {
        return isActive() && (
                role == AdminRole.ADMIN ||
                role == AdminRole.SUPER_ADMIN);
    }

    /** Can read and write document expiry configs. */
    public boolean canManageConfig() {
        return isActive() && (
                role == AdminRole.ADMIN ||
                role == AdminRole.SUPER_ADMIN);
    }

    /** Can create, suspend or remove other operators. SUPER_ADMIN only. */
    public boolean canManageAdmins() {
        return isActive() && role == AdminRole.SUPER_ADMIN;
    }

    // ── Domain behavior ───────────────────────────────────────────────────────

    public Admin suspend(String suspendedBy) {
        if (status == AdminStatus.SUSPENDED)
            throw new IllegalStateException("Operator is already suspended");
        return this.toBuilder()
                .status(AdminStatus.SUSPENDED)
                .updatedAt(DateUtil.now())
                .build();
    }

    public Admin reactivate(String reactivatedBy) {
        if (status == AdminStatus.ACTIVE)
            throw new IllegalStateException("Operator is already active");
        return this.toBuilder()
                .status(AdminStatus.ACTIVE)
                .updatedAt(DateUtil.now())
                .build();
    }

    /** Replaces the stored password hash. Hashing is done by the service layer. */
    public Admin changePassword(String newPasswordHash) {
        return this.toBuilder()
                .passwordHash(newPasswordHash)
                .updatedAt(DateUtil.now())
                .build();
    }

    // ── Factory ───────────────────────────────────────────────────────────────

    public static Admin create(IdGenerator ids, String fullName, String email,
                               AdminRole role, String passwordHash) {
        return Admin.builder()
                .id(ids.generate())
                .fullName(fullName)
                .email(email)
                .role(role)
                .status(AdminStatus.ACTIVE)
                .passwordHash(passwordHash)
                .createdAt(DateUtil.now())
                .build();
    }
}
