package com.pacta.pacta_app.admin.infrastructure.controller;

import com.pacta.pacta_app.admin.application.AdminAuthService;
import com.pacta.pacta_app.admin.application.dto.AdminAuthResponse;
import com.pacta.pacta_app.admin.application.dto.AdminLoginRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/admin")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AdminAuthService adminAuthService;

    /**
     * POST /api/auth/admin/login
     * Body: { "email": "...", "password": "..." }
     * Returns an operator JWT (type=operator) on success.
     */
    @PostMapping("/login")
    public AdminAuthResponse login(@Valid @RequestBody AdminLoginRequest req) {
        return adminAuthService.login(req.email(), req.password());
    }
}
