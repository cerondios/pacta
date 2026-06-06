package com.pacta.pacta_app.compliance.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

interface DocumentConfigSpringRepository extends JpaRepository<DocumentConfigJpaEntity, String> {}
