package com.pacta.pacta_app.tenant.application.dto;

import com.pacta.pacta_app.banking.application.dto.BankAccountResponse;
import com.pacta.pacta_app.compliance.application.dto.ComplianceDocResponse;
import com.pacta.pacta_app.kyc.application.dto.KycResponse;
import com.pacta.pacta_app.tenant.application.TenantService;
import com.pacta.pacta_app.user.domain.User;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public record TenantResponse(
        String id,
        String fullName,
        String email,
        String country,
        Set<String> roles,
        String status,
        int score,
        String createdAt,
        KycResponse kyc,
        List<ComplianceDocResponse> compliance,
        List<BankAccountResponse> bankAccounts
) {
    public static TenantResponse from(TenantService.TenantProfile profile) {
        User u = profile.user();
        return new TenantResponse(
                u.getId(), u.getFullName(), u.getEmail(),
                u.getCountry(),
                u.getRoles().stream().map(Enum::name).collect(Collectors.toSet()),
                u.getStatus().name(), u.getScore(), u.getCreatedAt(),
                profile.kyc() != null ? KycResponse.from(profile.kyc()) : null,
                profile.compliance().stream().map(ComplianceDocResponse::from).toList(),
                profile.bankAccounts().stream().map(BankAccountResponse::from).toList()
        );
    }
}
