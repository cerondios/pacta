package com.pacta.pacta_app.tenant.infrastructure.controller;

import com.pacta.pacta_app.tenant.application.TenantService;
import com.pacta.pacta_app.tenant.application.dto.TenantResponse;
import com.pacta.pacta_app.tenant.application.dto.TenantSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tenants")
@RequiredArgsConstructor
public class TenantController {

    private final TenantService tenantService;

    /** List all tenants (summary — no sensitive data). */
    @GetMapping
    public List<TenantSummaryResponse> listAll() {
        return tenantService.findAll().stream().map(TenantSummaryResponse::from).toList();
    }

    /** Full tenant profile: user data + KYC + compliance docs + bank accounts + score. */
    @GetMapping("/{id}")
    public TenantResponse getById(@PathVariable String id) {
        return TenantResponse.from(tenantService.findById(id));
    }

    /** Score only — useful for quick eligibility checks. */
    @GetMapping("/{id}/score")
    public ScoreResponse getScore(@PathVariable String id) {
        TenantService.TenantProfile profile = tenantService.findById(id);
        return new ScoreResponse(profile.user().getId(), profile.user().getScore());
    }

    public record ScoreResponse(String userId, int score) {}
}
