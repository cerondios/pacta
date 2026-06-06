package com.pacta.pacta_app.banking.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

interface BankAccountSpringRepository extends JpaRepository<BankAccountJpaEntity, String> {
    List<BankAccountJpaEntity> findByUserId(String userId);
}
