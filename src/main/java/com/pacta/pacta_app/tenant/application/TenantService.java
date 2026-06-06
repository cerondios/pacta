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

    public TenantProfile findById(String id) {
        User user = users.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Tenant not found: " + id));

        if (!user.hasRole(Role.TENANT)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tenant not found: " + id);
        }

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
