package com.pacta.pacta_app.tenant.application;

import com.pacta.pacta_app.banking.application.BankAccountService;
import com.pacta.pacta_app.banking.domain.BankAccount;
import com.pacta.pacta_app.compliance.application.ComplianceDocumentService;
import com.pacta.pacta_app.compliance.domain.ComplianceDocument;
import com.pacta.pacta_app.kyc.application.KycService;
import com.pacta.pacta_app.kyc.domain.KycDocument;
import com.pacta.pacta_app.user.domain.Role;
import com.pacta.pacta_app.user.domain.User;
import com.pacta.pacta_app.user.domain.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TenantService {

    private final IUserRepository            users;
    private final KycService                kycService;
    private final ComplianceDocumentService complianceService;
    private final BankAccountService        bankAccountService;

    public List<User> findAll() {
        return users.findAllByRole(Role.TENANT);
    }

    /**
     * Looks up the public profile of any platform user (tenant or landlord) — despite the
     * class name, this backs the generic {@code /api/users/{id}} endpoint, used e.g. to
     * resolve the counterpart's name on a Deal regardless of which side they're on.
     */
    public TenantProfile findById(String id) {
        User user = users.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "User not found: " + id));

        Optional<KycDocument>      kyc        = kycService.findByUserId(id);
        List<ComplianceDocument>   compliance = complianceService.findByUserId(id);
        List<BankAccount>          accounts   = bankAccountService.findByUserId(id);

        return new TenantProfile(user, kyc.orElse(null), compliance, accounts);
    }

    public record TenantProfile(
            User                     user,
            KycDocument              kyc,
            List<ComplianceDocument> compliance,
            List<BankAccount>        bankAccounts
    ) {}
}
