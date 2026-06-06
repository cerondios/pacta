package com.pacta.pacta_app.admin.infrastructure.persistence;

import com.pacta.pacta_app.admin.domain.Admin;
import com.pacta.pacta_app.admin.domain.AdminRole;
import com.pacta.pacta_app.admin.domain.AdminStatus;
import com.pacta.pacta_app.admin.domain.IAdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile({"dev", "prod"})
@RequiredArgsConstructor
public class PostgresAdminRepository implements IAdminRepository {

    private final AdminSpringRepository jpa;

    @Override public Admin save(Admin a)   { jpa.save(toEntity(a)); return a; }
    @Override public Admin update(Admin a) { jpa.save(toEntity(a)); return a; }

    @Override public Optional<Admin> findById(String id)      { return jpa.findById(id).map(this::toDomain); }
    @Override public Optional<Admin> findByEmail(String email){ return jpa.findByEmail(email).map(this::toDomain); }
    @Override public boolean existsByEmail(String email)      { return jpa.existsByEmail(email); }
    @Override public List<Admin> findAll()                    { return jpa.findAll().stream().map(this::toDomain).toList(); }
    @Override public List<Admin> findAllByRole(AdminRole role){ return jpa.findByRole(role.name()).stream().map(this::toDomain).toList(); }

    private AdminJpaEntity toEntity(Admin a) {
        return AdminJpaEntity.builder()
                .id(a.getId()).fullName(a.getFullName()).email(a.getEmail())
                .role(a.getRole().name()).status(a.getStatus().name())
                .passwordHash(a.getPasswordHash())
                .createdAt(a.getCreatedAt()).updatedAt(a.getUpdatedAt())
                .build();
    }

    private Admin toDomain(AdminJpaEntity e) {
        return Admin.builder()
                .id(e.getId()).fullName(e.getFullName()).email(e.getEmail())
                .role(AdminRole.valueOf(e.getRole())).status(AdminStatus.valueOf(e.getStatus()))
                .passwordHash(e.getPasswordHash())
                .createdAt(e.getCreatedAt()).updatedAt(e.getUpdatedAt())
                .build();
    }
}
