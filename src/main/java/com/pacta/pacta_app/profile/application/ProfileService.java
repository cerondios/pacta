package com.pacta.pacta_app.profile.application;

import com.pacta.pacta_app.banking.application.BankAccountService;
import com.pacta.pacta_app.banking.domain.AccountType;
import com.pacta.pacta_app.banking.domain.BankAccount;
import com.pacta.pacta_app.compliance.application.ComplianceDocumentService;
import com.pacta.pacta_app.compliance.application.dto.ComplianceRequirementResponse;
import com.pacta.pacta_app.compliance.domain.ComplianceDocument;
import com.pacta.pacta_app.kyc.application.KycService;
import com.pacta.pacta_app.kyc.domain.KycDocument;
import com.pacta.pacta_app.user.application.UserService;
import com.pacta.pacta_app.user.domain.Phone;
import com.pacta.pacta_app.user.domain.Role;
import com.pacta.pacta_app.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserService              userService;
    private final KycService               kycService;
    private final ComplianceDocumentService complianceService;
    private final BankAccountService       bankAccountService;

    // ── User profile ──────────────────────────────────────────────────────────

    @Transactional
    public Profile getProfile(String userId) {
        // Self-healing: repairs a user left at PENDING_KYC despite an already-APPROVED
        // KYC document (see KycService#ensureUserSyncedWithApprovedKyc). Idempotent.
        kycService.ensureUserSyncedWithApprovedKyc(userId);

        // Self-healing: catches users left in ACTIVE whose completion was never
        // re-evaluated after their last KYC/document approval. Idempotent — a
        // no-op once the user is actually COMPLETED or still has pending requirements.
        complianceService.evaluateCompletion(userId, "system");

        User user              = userService.getById(userId);
        Optional<KycDocument> kyc = kycService.findByUserId(userId);
        int pendingDocuments   = (int) complianceService.getRequirements(userId).stream()
                .filter(r -> "NOT_SUBMITTED".equals(r.status())).count();
        List<BankAccount> accounts = bankAccountService.findByUserId(userId);
        return new Profile(user, kyc.orElse(null), pendingDocuments, accounts);
    }

    @Transactional
    public User updatePersonalInfo(String userId, String fullName, Phone phone) {
        return userService.updateProfile(userId, fullName, phone, userId);
    }

    /**
     * One-time onboarding completion — sets roles and city.
     * Delegates the immutability constraint to the domain.
     */
    @Transactional
    public User completeOnboarding(String userId, Set<Role> roles, String city) {
        return userService.completeOnboarding(userId, roles, city);
    }

    // ── KYC ───────────────────────────────────────────────────────────────────

    @Transactional
    public KycDocument submitKyc(String userId, String frontKey, String rearKey, String selfieKey) {
        return kycService.submit(userId, frontKey, rearKey, selfieKey);
    }

    public Optional<KycDocument> getKyc(String userId) {
        return kycService.findByUserId(userId);
    }

    // ── Compliance documents ──────────────────────────────────────────────────

    @Transactional
    public ComplianceDocument submitDocument(String userId, String typeCode, String key, Instant issuedAt) {
        return complianceService.submit(userId, typeCode, key, issuedAt);
    }

    public List<ComplianceRequirementResponse> getDocuments(String userId) {
        return complianceService.getRequirements(userId);
    }

    // ── Bank accounts ─────────────────────────────────────────────────────────

    @Transactional
    public BankAccount addBankAccount(String userId, String bankName, String accountNumber,
                                      AccountType accountType, String holderName) {
        return bankAccountService.add(userId, bankName, accountNumber, accountType, holderName);
    }

    @Transactional
    public void removeBankAccount(String userId, String accountId) {
        bankAccountService.remove(userId, accountId);
    }

    public List<BankAccount> getBankAccounts(String userId) {
        return bankAccountService.findByUserId(userId);
    }

    // ── Aggregate ─────────────────────────────────────────────────────────────

    public record Profile(
            User             user,
            KycDocument      kyc,
            int              pendingDocuments,
            List<BankAccount> bankAccounts
    ) {}
}
