package com.pacta.pacta_app.user.infrastructure.controller;

import com.pacta.pacta_app.tenant.application.TenantService;
import com.pacta.pacta_app.tenant.application.dto.TenantPublicResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final TenantService tenantService;

    /** Public profile — any authenticated user can see name, score, status, KYC and compliance status. */
    @GetMapping("/{id}")
    public TenantPublicResponse getPublicProfile(@PathVariable String id) {
        return TenantPublicResponse.from(tenantService.findById(id));
    }
}
