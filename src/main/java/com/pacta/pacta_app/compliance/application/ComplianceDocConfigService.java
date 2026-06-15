package com.pacta.pacta_app.compliance.application;

import com.pacta.pacta_app.admin.domain.IAdminRepository;
import com.pacta.pacta_app.compliance.domain.ComplianceDocConfig;
import com.pacta.pacta_app.compliance.domain.IComplianceDocConfigRepository;
import com.pacta.pacta_app.shared.domain.IdGenerator;
import com.pacta.pacta_app.shared.domain.MetricRecorder;
import com.pacta.pacta_app.user.domain.IUserRepository;
import com.pacta.pacta_app.user.domain.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ComplianceDocConfigService {

    private final IComplianceDocConfigRepository docConfigs;
    private final IAdminRepository               admins;
    private final IUserRepository                users;
    private final MetricRecorder                 metrics;
    private final IdGenerator                    ids;

    public List<ComplianceDocConfig> findAll(String operatorId) {
        requireManageConfig(operatorId);
        return docConfigs.findAll();
    }

    public List<ComplianceDocConfig> findByCountry(String operatorId, String countryCode) {
        requireManageConfig(operatorId);
        return docConfigs.findByCountryCode(countryCode);
    }

    /**
     * A new required document type means every already-COMPLETED user in that
     * country now has an unmet requirement — drop them back to ACTIVE so they
     * re-enter the compliance flow.
     */
    @Transactional
    public ComplianceDocConfig create(String operatorId, String countryCode, String typeCode, String displayName) {
        requireManageConfig(operatorId);
        if (docConfigs.findByCountryCodeAndTypeCode(countryCode, typeCode).isPresent())
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Config already exists for " + countryCode + "/" + typeCode);
        ComplianceDocConfig saved = docConfigs.save(ComplianceDocConfig.builder()
                .id(ids.generate())
                .countryCode(countryCode)
                .typeCode(typeCode)
                .displayName(displayName)
                .build());

        users.findByCountryAndStatus(countryCode, UserStatus.COMPLETED).forEach(user -> {
            users.update(user.downgradeToActive());
            metrics.incrementCounter("user.downgraded_to_active", "reason", "new_required_document");
        });

        return saved;
    }

    public void delete(String operatorId, String id) {
        requireManageConfig(operatorId);
        if (docConfigs.findById(id).isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Config not found: " + id);
        docConfigs.delete(id);
    }

    private void requireManageConfig(String operatorId) {
        var admin = admins.findById(operatorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "Operator not found: " + operatorId));
        if (!admin.canManageConfig())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Only ADMIN or SUPER_ADMIN can manage compliance configs");
    }
}
