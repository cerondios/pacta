package com.pacta.pacta_app.profile.infrastructure.controller;

import com.pacta.pacta_app.banking.application.dto.BankAccountResponse;
import com.pacta.pacta_app.banking.domain.AccountType;
import com.pacta.pacta_app.compliance.application.dto.ComplianceDocResponse;
import com.pacta.pacta_app.compliance.domain.DocumentType;
import com.pacta.pacta_app.kyc.application.dto.KycResponse;
import com.pacta.pacta_app.profile.application.ProfileService;
import com.pacta.pacta_app.shared.infrastructure.filter.PactaTokenFilter;
import com.pacta.pacta_app.user.domain.Phone;
import com.pacta.pacta_app.user.domain.Role;
import com.pacta.pacta_app.user.domain.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    // ── Full profile ──────────────────────────────────────────────────────────

    @GetMapping
    public ProfileResponse getProfile(@RequestHeader(PactaTokenFilter.HEADER_USER_ID) String userId) {
        ProfileService.Profile p = profileService.getProfile(userId);
        return ProfileResponse.from(p);
    }

    @PutMapping
    public ProfileResponse updatePersonalInfo(@RequestHeader(PactaTokenFilter.HEADER_USER_ID) String userId,
                                              @Valid @RequestBody UpdatePersonalInfoRequest req) {
        Phone phone = req.phone() != null ? new Phone(req.phone().indicative(), req.phone().number()) : null;
        User updated = profileService.updatePersonalInfo(userId, req.fullName(), phone);
        ProfileService.Profile p = profileService.getProfile(updated.getId());
        return ProfileResponse.from(p);
    }

    // ── Onboarding ────────────────────────────────────────────────────────────

    /**
     * One-time call that sets roles and city for a newly registered user.
     * Returns 409 Conflict if onboarding was already completed.
     */
    @PostMapping("/onboarding")
    @ResponseStatus(HttpStatus.OK)
    public ProfileResponse completeOnboarding(@RequestHeader(PactaTokenFilter.HEADER_USER_ID) String userId,
                                              @Valid @RequestBody OnboardingRequest req) {
        User updated = profileService.completeOnboarding(userId, req.roles(), req.city());
        ProfileService.Profile p = profileService.getProfile(updated.getId());
        return ProfileResponse.from(p);
    }

    // ── KYC ───────────────────────────────────────────────────────────────────

    @PostMapping("/kyc")
    @ResponseStatus(HttpStatus.CREATED)
    public KycResponse submitKyc(@RequestHeader(PactaTokenFilter.HEADER_USER_ID) String userId,
                                 @Valid @RequestBody KycRequest req) {
        return KycResponse.from(profileService.submitKyc(userId, req.frontKey(), req.rearKey(), req.selfieKey()));
    }

    @GetMapping("/kyc")
    public KycResponse getKyc(@RequestHeader(PactaTokenFilter.HEADER_USER_ID) String userId) {
        return profileService.getKyc(userId)
                .map(KycResponse::from)
                .orElse(null);
    }

    // ── Compliance documents ──────────────────────────────────────────────────

    @PostMapping("/documents")
    @ResponseStatus(HttpStatus.CREATED)
    public ComplianceDocResponse submitDocument(@RequestHeader(PactaTokenFilter.HEADER_USER_ID) String userId,
                                                @Valid @RequestBody DocumentRequest req) {
        return ComplianceDocResponse.from(
                profileService.submitDocument(userId, req.type(), req.key(), req.issuedAt()));
    }

    @GetMapping("/documents")
    public List<ComplianceDocResponse> getDocuments(@RequestHeader(PactaTokenFilter.HEADER_USER_ID) String userId) {
        return profileService.getDocuments(userId).stream().map(ComplianceDocResponse::from).toList();
    }

    // ── Bank accounts ─────────────────────────────────────────────────────────

    @PostMapping("/bank-accounts")
    @ResponseStatus(HttpStatus.CREATED)
    public BankAccountResponse addBankAccount(@RequestHeader(PactaTokenFilter.HEADER_USER_ID) String userId,
                                              @Valid @RequestBody BankAccountRequest req) {
        return BankAccountResponse.from(
                profileService.addBankAccount(userId, req.bankName(), req.accountNumber(),
                        req.accountType(), req.holderName()));
    }

    @GetMapping("/bank-accounts")
    public List<BankAccountResponse> getBankAccounts(@RequestHeader(PactaTokenFilter.HEADER_USER_ID) String userId) {
        return profileService.getBankAccounts(userId).stream().map(BankAccountResponse::from).toList();
    }

    @DeleteMapping("/bank-accounts/{accountId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeBankAccount(@PathVariable String accountId,
                                  @RequestHeader(PactaTokenFilter.HEADER_USER_ID) String userId) {
        profileService.removeBankAccount(userId, accountId);
    }

    // ── DTOs ──────────────────────────────────────────────────────────────────

    public record OnboardingRequest(
            @NotEmpty Set<Role> roles,
            @NotBlank String city
    ) {}

    public record ProfileResponse(
            String id,
            String fullName,
            String email,
            PhoneDto phone,
            String country,
            String city,
            Set<String> roles,
            String status,
            int score,
            String createdAt,
            KycResponse kyc,
            List<ComplianceDocResponse> documents,
            List<BankAccountResponse> bankAccounts
    ) {
        static ProfileResponse from(ProfileService.Profile p) {
            User u = p.user();
            return new ProfileResponse(
                    u.getId(), u.getFullName(), u.getEmail(),
                    u.getPhone() != null ? new PhoneDto(u.getPhone().indicative(), u.getPhone().number()) : null,
                    u.getCountry(),
                    u.getCity(),
                    u.getRoles().stream().map(Enum::name).collect(Collectors.toSet()),
                    u.getStatus().name(), u.getScore(), u.getCreatedAt(),
                    p.kyc() != null ? KycResponse.from(p.kyc()) : null,
                    p.documents().stream().map(ComplianceDocResponse::from).toList(),
                    p.bankAccounts().stream().map(BankAccountResponse::from).toList()
            );
        }
    }

    public record PhoneDto(String indicative, String number) {}

    public record UpdatePersonalInfoRequest(
            @NotBlank String fullName,
            PhoneDto phone
    ) {}

    public record KycRequest(
            @NotBlank String frontKey,
            @NotBlank String rearKey,
            @NotBlank String selfieKey
    ) {}

    public record DocumentRequest(
            @NotNull DocumentType type,
            @NotBlank String key,
            @NotNull Instant issuedAt
    ) {}

    public record BankAccountRequest(
            @NotBlank String bankName,
            @NotBlank String accountNumber,
            @NotNull AccountType accountType,
            @NotBlank String holderName
    ) {}
}
