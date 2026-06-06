package com.pacta.pacta_app.admin.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

interface AdminSpringRepository extends JpaRepository<AdminJpaEntity, String> {
    Optional<AdminJpaEntity> findByEmail(String email);
    boolean existsByEmail(String email);
    List<AdminJpaEntity> findByRole(String role);
}
