package com.pacta.pacta_app.pricing.infrastructure.controller;

import com.pacta.pacta_app.pricing.application.PricingService;
import com.pacta.pacta_app.pricing.application.dto.PricingBreakdown;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pricing")
@RequiredArgsConstructor
public class PricingController {

    private final PricingService pricingService;

    /** Computes the Pacta commission breakdown for a given monthly rent and country. */
    @GetMapping("/breakdown")
    public PricingBreakdown getBreakdown(
            @RequestParam(name = "monthly_rent") long monthlyRent,
            @RequestParam String country) {
        return pricingService.calculateBreakdown(monthlyRent, country);
    }
}
