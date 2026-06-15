package com.pacta.pacta_app.pricing.application;

import com.pacta.pacta_app.admin.domain.IAdminRepository;
import com.pacta.pacta_app.pricing.application.dto.PricingBreakdown;
import com.pacta.pacta_app.pricing.domain.IPricingConfigRepository;
import com.pacta.pacta_app.pricing.domain.PricingConfig;
import com.pacta.pacta_app.shared.domain.DateUtil;
import com.pacta.pacta_app.shared.domain.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PricingService {

    /** Used when no per-country config exists yet. */
    public static final double DEFAULT_COMMISSION_PERCENTAGE = 8.0;

    private final IPricingConfigRepository pricingConfigs;
    private final IAdminRepository         admins;
    private final IdGenerator              ids;

    public double getCommissionPercentage(String countryCode) {
        return pricingConfigs.findByCountryCode(countryCode)
                .map(PricingConfig::getCommissionPercentage)
                .orElse(DEFAULT_COMMISSION_PERCENTAGE);
    }

    public PricingBreakdown calculateBreakdown(long monthlyRent, String countryCode) {
        double commissionPercentage = getCommissionPercentage(countryCode);
        long pactaFee = java.math.BigDecimal.valueOf(monthlyRent)
                .multiply(java.math.BigDecimal.valueOf(commissionPercentage))
                .divide(java.math.BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP)
                .longValueExact();
        return new PricingBreakdown(monthlyRent, commissionPercentage, pactaFee, monthlyRent - pactaFee);
    }

    // ── Admin config management ──────────────────────────────────────────────

    public List<PricingConfig> findAll(String operatorId) {
        requireManageConfig(operatorId);
        return pricingConfigs.findAll();
    }

    public PricingConfig upsert(String operatorId, String countryCode, double commissionPercentage) {
        requireManageConfig(operatorId);
        var existing = pricingConfigs.findByCountryCode(countryCode);
        PricingConfig config = existing
                .map(c -> c.toBuilder().commissionPercentage(commissionPercentage).updatedAt(DateUtil.now()).build())
                .orElseGet(() -> PricingConfig.builder()
                        .id(ids.generate())
                        .countryCode(countryCode)
                        .commissionPercentage(commissionPercentage)
                        .createdAt(DateUtil.now())
                        .build());
        return pricingConfigs.save(config);
    }

    public void delete(String operatorId, String id) {
        requireManageConfig(operatorId);
        if (pricingConfigs.findById(id).isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pricing config not found: " + id);
        pricingConfigs.delete(id);
    }

    private void requireManageConfig(String operatorId) {
        var admin = admins.findById(operatorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "Operator not found: " + operatorId));
        if (!admin.canManageConfig())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Only ADMIN or SUPER_ADMIN can manage pricing configs");
    }
}
