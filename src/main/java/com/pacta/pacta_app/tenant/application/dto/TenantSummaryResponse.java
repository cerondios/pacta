package com.pacta.pacta_app.tenant.application.dto;

import com.pacta.pacta_app.user.domain.User;

import java.util.Set;
import java.util.stream.Collectors;

public record TenantSummaryResponse(
        String id,
        String fullName,
        String email,
        String country,
        Set<String> roles,
        String status,
        int score
) {
    public static TenantSummaryResponse from(User u) {
        return new TenantSummaryResponse(
                u.getId(), u.getFullName(), u.getEmail(),
                u.getCountry(),
                u.getRoles().stream().map(Enum::name).collect(Collectors.toSet()),
                u.getStatus().name(), u.getScore());
    }
}
