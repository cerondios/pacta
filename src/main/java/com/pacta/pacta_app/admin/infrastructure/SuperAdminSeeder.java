package com.pacta.pacta_app.admin.infrastructure;

import com.pacta.pacta_app.admin.domain.Admin;
import com.pacta.pacta_app.admin.domain.AdminRole;
import com.pacta.pacta_app.admin.domain.IAdminRepository;
import com.pacta.pacta_app.shared.domain.IdGenerator;
import com.pacta.pacta_app.shared.domain.MetricRecorder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Seeds the very first SUPER_ADMIN on application startup — only if none exists.
 *
 * Required env vars:
 *   PACTA_SUPER_ADMIN_EMAIL     e.g. super@pacta.app
 *   PACTA_SUPER_ADMIN_PASSWORD  e.g. Ch@ngeMe123  (min 8 chars)
 *
 * This is idempotent: if a SUPER_ADMIN already exists the seeder does nothing.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SuperAdminSeeder implements ApplicationRunner {

    private final IAdminRepository admins;
    private final PasswordEncoder  passwordEncoder;
    private final IdGenerator      ids;
    private final MetricRecorder   metrics;

    @Value("${pacta.super-admin.email}")
    private String superAdminEmail;

    @Value("${pacta.super-admin.password}")
    private String superAdminPassword;

    @Override
    public void run(ApplicationArguments args) {
        boolean alreadyExists = !admins.findAllByRole(AdminRole.SUPER_ADMIN).isEmpty();
        if (alreadyExists) {
            log.debug("SuperAdminSeeder: SUPER_ADMIN already exists — skipping");
            return;
        }

        if (superAdminEmail == null || superAdminEmail.isBlank()) {
            log.warn("SuperAdminSeeder: no SUPER_ADMIN found and PACTA_SUPER_ADMIN_EMAIL is not set — skipping");
            return;
        }
        if (superAdminPassword == null || superAdminPassword.isBlank()) {
            log.warn("SuperAdminSeeder: PACTA_SUPER_ADMIN_PASSWORD is not set — skipping");
            return;
        }

        Admin superAdmin = Admin.create(
                ids,
                "Super Admin",
                superAdminEmail.trim().toLowerCase(),
                AdminRole.SUPER_ADMIN,
                passwordEncoder.encode(superAdminPassword));

        admins.save(superAdmin);
        metrics.incrementCounter("admin.super_admin.seeded");
    }
}
