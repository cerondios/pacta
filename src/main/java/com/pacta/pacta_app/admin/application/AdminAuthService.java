package com.pacta.pacta_app.admin.application;

import com.pacta.pacta_app.admin.application.dto.AdminAuthResponse;
import com.pacta.pacta_app.admin.domain.Admin;
import com.pacta.pacta_app.admin.domain.IAdminRepository;
import com.pacta.pacta_app.admin.infrastructure.AdminJwtService;
import com.pacta.pacta_app.shared.domain.MetricRecorder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Authenticates internal operators via email + password.
 * Returns an operator JWT (type=operator) on success.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminAuthService {

    private final IAdminRepository admins;
    private final PasswordEncoder  passwordEncoder;
    private final AdminJwtService  jwt;
    private final MetricRecorder   metrics;

    public AdminAuthResponse login(String email, String rawPassword) {
        String normalized = email.trim().toLowerCase();

        Admin admin = admins.findByEmail(normalized)
                .orElseThrow(() -> unauthorized("not_found"));

        if (!admin.isActive()) {
            throw unauthorized("suspended");
        }

        if (admin.getPasswordHash() == null ||
                !passwordEncoder.matches(rawPassword, admin.getPasswordHash())) {
            metrics.incrementCounter("admin.auth.failure");
            throw unauthorized("bad_credentials");
        }

        metrics.incrementCounter("admin.auth.success", "role", admin.getRole().name());
        log.info("Operator login id={} role={}", admin.getId(), admin.getRole());
        return AdminAuthResponse.bearer(jwt.generate(admin), jwt.ttlSeconds());
    }

    private ResponseStatusException unauthorized(String reason) {
        metrics.incrementCounter("admin.auth.failure", "reason", reason);
        // Always return the same message — do not leak whether the email exists
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
    }
}
