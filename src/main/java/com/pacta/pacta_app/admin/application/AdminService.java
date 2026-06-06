package com.pacta.pacta_app.admin.application;

import com.pacta.pacta_app.admin.domain.Admin;
import com.pacta.pacta_app.admin.domain.IAdminRepository;
import com.pacta.pacta_app.admin.domain.AdminRole;
import com.pacta.pacta_app.shared.domain.IdGenerator;
import com.pacta.pacta_app.shared.domain.MetricRecorder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final IAdminRepository admins;
    private final IdGenerator      ids;
    private final MetricRecorder   metrics;
    private final PasswordEncoder  passwordEncoder;

    /**
     * Creates a new operator. Only SUPER_ADMINs may call this.
     *
     * @param operatorId  ID of the operator making the request (from X-Operator-Id header)
     * @param fullName  Display name
     * @param email     Login email
     * @param role      Role to assign
     * @param rawPassword Plain-text password (will be hashed)
     */
    @Transactional
    public Admin create(String operatorId, String fullName, String email,
                        AdminRole role, String rawPassword) {
        Admin caller = admins.findById(operatorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "Operator not found: " + operatorId));

        if (!caller.canManageAdmins()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Only SUPER_ADMINs can create operators");
        }

        String normalizedEmail = email.trim().toLowerCase();
        if (admins.existsByEmail(normalizedEmail)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Email already registered: " + normalizedEmail);
        }

        Admin admin = Admin.create(ids, fullName, normalizedEmail, role,
                passwordEncoder.encode(rawPassword));
        admins.save(admin);
        metrics.incrementCounter("admin.created", "role", role.name());
        return admin;
    }

    public Admin getById(String id) {
        return admins.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Operator not found: " + id));
    }

    public Admin getByEmail(String email) {
        return admins.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Operator not found: " + email));
    }

    public List<Admin> findAll() {
        return admins.findAll();
    }

    public List<Admin> findAllByRole(AdminRole role) {
        return admins.findAllByRole(role);
    }
}
