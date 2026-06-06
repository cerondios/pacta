package com.pacta.pacta_app.user.infrastructure.controller;

import com.pacta.pacta_app.banking.application.BankAccountService;
import com.pacta.pacta_app.banking.application.dto.BankAccountResponse;
import com.pacta.pacta_app.compliance.application.ComplianceDocumentService;
import com.pacta.pacta_app.compliance.application.dto.ComplianceDocResponse;
import com.pacta.pacta_app.kyc.application.KycService;
import com.pacta.pacta_app.kyc.application.dto.KycResponse;
import com.pacta.pacta_app.shared.infrastructure.filter.PactaTokenFilter;
import com.pacta.pacta_app.user.application.UserService;
import com.pacta.pacta_app.user.application.dto.*;
import com.pacta.pacta_app.user.domain.Phone;
import com.pacta.pacta_app.user.domain.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService              userService;
    private final KycService               kycService;
    private final ComplianceDocumentService complianceService;
    private final BankAccountService       bankAccountService;

    @GetMapping("/me")
    public UserResponse me(@RequestHeader(PactaTokenFilter.HEADER_USER_ID) String userId) {
        return fullResponse(userId);
    }

    @PutMapping("/me")
    public UserResponse updateMe(@RequestHeader(PactaTokenFilter.HEADER_USER_ID) String userId,
                                 @Valid @RequestBody UpdateProfileRequest req) {
        Phone phone = req.phone() != null ? req.phone().toDomain() : null;
        userService.updateProfile(userId, req.fullName(), phone, userId);
        return basicResponse(userService.getById(userId));
    }

    @GetMapping
    public List<UserResponse> listAll() {
        return userService.listAll().stream().map(this::basicResponse).toList();
    }

    @GetMapping("/{id}")
    public UserResponse getById(@PathVariable String id) {
        return fullResponse(id);
    }

    @PatchMapping("/{id}/block")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void block(@PathVariable String id,
                      @RequestHeader(PactaTokenFilter.HEADER_USER_ID) String adminId) {
        userService.block(id, adminId);
    }

    @PatchMapping("/{id}/unblock")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unblock(@PathVariable String id,
                        @RequestHeader(PactaTokenFilter.HEADER_USER_ID) String adminId) {
        userService.unblock(id, adminId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable String id,
                       @RequestBody RemoveUserRequest req,
                       @RequestHeader(PactaTokenFilter.HEADER_USER_ID) String adminId) {
        userService.remove(id, req.reason(), adminId);
    }

    // ── Full profile assembly ─────────────────────────────────────────────────

    private UserResponse fullResponse(String userId) {
        User user = userService.getById(userId);
        KycResponse kyc = kycService.findByUserId(userId).map(KycResponse::from).orElse(null);
        List<ComplianceDocResponse> compliance = complianceService.findByUserId(userId)
                .stream().map(ComplianceDocResponse::from).toList();
        List<BankAccountResponse> accounts = bankAccountService.findByUserId(userId)
                .stream().map(BankAccountResponse::from).toList();
        return toResponse(user, kyc, compliance, accounts);
    }

    private UserResponse basicResponse(User u) {
        return toResponse(u, null, null, null);
    }

    private UserResponse toResponse(User u, KycResponse kyc,
                                    List<ComplianceDocResponse> compliance,
                                    List<BankAccountResponse> accounts) {
        return new UserResponse(
                u.getId(), u.getFullName(), u.getEmail(),
                u.getPhone() != null ? new PhoneDto(u.getPhone().indicative(), u.getPhone().number()) : null,
                u.getCountry(),
                u.getRoles().stream().map(Enum::name).collect(Collectors.toSet()),
                u.getStatus().name(), u.getScore(),
                u.getCreatedAt(), u.getUpdatedAt(),
                kyc, compliance, accounts);
    }

    // ── DTOs ──────────────────────────────────────────────────────────────────

    public record UserResponse(
            String id, String fullName, String email,
            PhoneDto phone, String country,
            Set<String> roles, String status, int score,
            String createdAt, String updatedAt,
            KycResponse kyc,
            List<ComplianceDocResponse> compliance,
            List<BankAccountResponse> bankAccounts
    ) {}
}
