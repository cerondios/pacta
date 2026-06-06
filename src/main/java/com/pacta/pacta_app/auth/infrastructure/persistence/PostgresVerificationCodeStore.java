package com.pacta.pacta_app.auth.infrastructure.persistence;

import com.pacta.pacta_app.auth.domain.IVerificationCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
@Profile({"dev", "prod"})
@RequiredArgsConstructor
public class PostgresVerificationCodeStore implements IVerificationCodeRepository {

    private final VerificationCodeJpaRepository jpa;

    @Override
    public void save(String email, String codeHash, String expiresAt) {
        jpa.save(VerificationCodeJpaEntity.builder()
                .email(email.toLowerCase())
                .codeHash(codeHash)
                .expiresAt(expiresAt)
                .build());
    }

    @Override
    public Optional<Entry> find(String email) {
        return jpa.findById(email.toLowerCase())
                .map(e -> new Entry(e.getCodeHash(), e.getExpiresAt()));
    }

    @Override
    public void remove(String email) {
        jpa.deleteById(email.toLowerCase());
    }
}
