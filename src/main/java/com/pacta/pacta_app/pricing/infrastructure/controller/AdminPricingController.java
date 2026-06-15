package com.pacta.pacta_app.pricing.infrastructure.controller;

import com.pacta.pacta_app.pricing.application.PricingService;
import com.pacta.pacta_app.pricing.domain.PricingConfig;
import com.pacta.pacta_app.shared.infrastructure.filter.OperatorTokenFilter;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/pricing/configs")
@RequiredArgsConstructor
public class AdminPricingController {

    private final PricingService pricingService;

    @GetMapping
    public List<PricingConfigResponse> findAll(
            @RequestHeader(OperatorTokenFilter.HEADER_OPERATOR_ID) String operatorId) {
        return pricingService.findAll(operatorId).stream().map(PricingConfigResponse::from).toList();
    }

    /** Creates the config for a country, or updates the commission if one already exists. */
    @PutMapping
    public PricingConfigResponse upsert(
            @RequestHeader(OperatorTokenFilter.HEADER_OPERATOR_ID) String operatorId,
            @RequestBody UpsertPricingConfigRequest req) {
        return PricingConfigResponse.from(
                pricingService.upsert(operatorId, req.countryCode(), req.commissionPercentage()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable String id,
            @RequestHeader(OperatorTokenFilter.HEADER_OPERATOR_ID) String operatorId) {
        pricingService.delete(operatorId, id);
    }

    public record UpsertPricingConfigRequest(
            @NotBlank String countryCode,
            double commissionPercentage
    ) {}

    public record PricingConfigResponse(
            String id, String countryCode, double commissionPercentage,
            String createdAt, String updatedAt
    ) {
        static PricingConfigResponse from(PricingConfig c) {
            return new PricingConfigResponse(
                    c.getId(), c.getCountryCode(), c.getCommissionPercentage(),
                    c.getCreatedAt(), c.getUpdatedAt());
        }
    }
}
