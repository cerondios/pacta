package com.pacta.pacta_app.landlord.application.dto;

import com.pacta.pacta_app.user.domain.User;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

public record LandlordResponse(
        String id,
        String fullName,
        String email,
        String country,
        Set<String> roles,
        String status,
        int score,
        String createdAt
) {
    public static LandlordResponse from(User u) {
        return new LandlordResponse(
                u.getId(), u.getFullName(), u.getEmail(),
                u.getCountry(),
                u.getRoles().stream().map(Enum::name).collect(Collectors.toSet()),
                u.getStatus().name(), u.getScore(),
                u.getCreatedAt());
    }
}
