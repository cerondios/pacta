package com.pacta.pacta_app.profile.application;

import com.pacta.pacta_app.banking.application.BankAccountService;
import com.pacta.pacta_app.banking.domain.AccountType;
import com.pacta.pacta_app.banking.domain.BankAccount;
import com.pacta.pacta_app.compliance.application.ComplianceDocumentService;
import com.pacta.pacta_app.compliance.domain.ComplianceDocument;
import com.pacta.pacta_app.compliance.domain.DocumentType;
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

    public Profile getProfile(String userId) {
        User user                    = userService.getById(userId);
        Optional<KycDocument> kyc    = kycService.findByUserId(userId);
        List<ComplianceDocument> docs = complianceService.findByUserId(userId);
        List<BankAccount> accounts   = bankAccountService.findByUserId(userId);
        return new Profile(user, kyc.orElse(null), docs, accounts);
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
    public ComplianceDocument submitDocument(String userId, DocumentType type, String key, Instant issuedAt) {
        return complianceService.submit(userId, type, key, issuedAt);
    }

    public List<ComplianceDocument> getDocuments(String userId) {
        return complianceService.findByUserId(userId);
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
            User                     user,
            KycDocument              kyc,
            List<ComplianceDocument> documents,
            List<BankAccount>        bankAccounts
    ) {}
}
