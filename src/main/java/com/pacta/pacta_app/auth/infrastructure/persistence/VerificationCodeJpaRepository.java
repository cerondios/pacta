package com.pacta.pacta_app.auth.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

interface VerificationCodeJpaRepository extends JpaRepository<VerificationCodeJpaEntity, String> {}
