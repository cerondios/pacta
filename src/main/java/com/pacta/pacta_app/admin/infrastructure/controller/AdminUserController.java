package com.pacta.pacta_app.admin.infrastructure.controller;

import com.pacta.pacta_app.banking.application.BankAccountService;
import com.pacta.pacta_app.banking.application.dto.BankAccountResponse;
import com.pacta.pacta_app.compliance.application.ComplianceDocumentService;
import com.pacta.pacta_app.compliance.application.dto.ComplianceDocResponse;
import com.pacta.pacta_app.kyc.application.KycService;
import com.pacta.pacta_app.kyc.application.dto.KycResponse;
import com.pacta.pacta_app.shared.infrastructure.filter.OperatorTokenFilter;
import com.pacta.pacta_app.user.application.UserService;
import com.pacta.pacta_app.user.application.dto.PhoneDto;
import com.pacta.pacta_app.user.application.dto.RemoveUserRequest;
import com.pacta.pacta_app.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService               userService;
    private final KycService                kycService;
    private final ComplianceDocumentService complianceService;
    private final BankAccountService        bankAccountService;

    @GetMapping
    public List<AdminUserResponse> listAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String role) {
        return userService.search(name, status, role).stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    public AdminUserResponse getById(@PathVariable String id) {
        return fullResponse(id);
    }

    @PatchMapping("/{id}/block")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void block(@PathVariable String id,
                      @RequestHeader(OperatorTokenFilter.HEADER_OPERATOR_ID) String adminId) {
        userService.block(id, adminId);
    }

    @PatchMapping("/{id}/unblock")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unblock(@PathVariable String id,
                        @RequestHeader(OperatorTokenFilter.HEADER_OPERATOR_ID) String adminId) {
        userService.unblock(id, adminId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable String id,
                       @RequestBody RemoveUserRequest req,
                       @RequestHeader(OperatorTokenFilter.HEADER_OPERATOR_ID) String adminId) {
        userService.remove(id, req.reason(), adminId);
    }

    // ── Private ───────────────────────────────────────────────────────────────

    private AdminUserResponse fullResponse(String userId) {
        User user = userService.getById(userId);
        KycResponse kyc = kycService.findByUserId(userId).map(KycResponse::from).orElse(null);
        List<ComplianceDocResponse> compliance = complianceService.findByUserId(userId)
                .stream().map(ComplianceDocResponse::from).toList();
        List<BankAccountResponse> accounts = bankAccountService.findByUserId(userId)
                .stream().map(BankAccountResponse::from).toList();
        return new AdminUserResponse(
                user.getId(), user.getFullName(), user.getEmail(),
                user.getPhone() != null ? new PhoneDto(user.getPhone().indicative(), user.getPhone().number()) : null,
                user.getCountry(),
                user.getRoles().stream().map(Enum::name).collect(Collectors.toSet()),
                user.getStatus().name(), user.getScore(),
                user.getCreatedAt(), user.getUpdatedAt(),
                kyc, compliance, accounts);
    }

    private AdminUserResponse toResponse(User u) {
        return new AdminUserResponse(
                u.getId(), u.getFullName(), u.getEmail(),
                u.getPhone() != null ? new PhoneDto(u.getPhone().indicative(), u.getPhone().number()) : null,
                u.getCountry(),
                u.getRoles().stream().map(Enum::name).collect(Collectors.toSet()),
                u.getStatus().name(), u.getScore(),
                u.getCreatedAt(), u.getUpdatedAt(),
                null, null, null);
    }

    public record AdminUserResponse(
            String id, String fullName, String email,
            PhoneDto phone, String country,
            Set<String> roles, String status, int score,
            String createdAt, String updatedAt,
            KycResponse kyc,
            List<ComplianceDocResponse> compliance,
            List<BankAccountResponse> bankAccounts
    ) {}
}
