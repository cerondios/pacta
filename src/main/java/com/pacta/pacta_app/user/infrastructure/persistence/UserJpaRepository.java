package com.pacta.pacta_app.user.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

interface UserJpaRepository extends JpaRepository<UserJpaEntity, String> {
    Optional<UserJpaEntity> findByEmail(String email);
    boolean existsByEmail(String email);

    @Query("SELECT DISTINCT u FROM UserJpaEntity u JOIN u.roles r WHERE r = :role")
    List<UserJpaEntity> findAllByRole(String role);
}
