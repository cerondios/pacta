package com.pacta.pacta_app.admin.application.dto;

import com.pacta.pacta_app.admin.domain.Admin;

public record AdminResponse(
        String id,
        String fullName,
        String email,
        String role,
        String status,
        String createdAt
) {
    public static AdminResponse from(Admin a) {
        return new AdminResponse(a.getId(), a.getFullName(), a.getEmail(),
                a.getRole().name(), a.getStatus().name(), a.getCreatedAt());
    }
}
