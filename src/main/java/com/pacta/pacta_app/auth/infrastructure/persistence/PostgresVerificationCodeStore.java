package com.pacta.pacta_app.auth.infrastructure.persistence;

import com.pacta.pacta_app.auth.domain.VerificationCodeStore;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
@Primary
@Profile({"dev", "prod"})
@RequiredArgsConstructor
public class PostgresVerificationCodeStore implements VerificationCodeStore {

    private final VerificationCodeJpaRepository jpa;

    @Override
    public void save(String email, String codeHash, Instant expiresAt) {
        jpa.save(VerificationCodeJpaEntity.builder()
                .email(email)
                .codeHash(codeHash)
                .expiresAt(expiresAt)
                .build());
    }

    @Override
    public Optional<Entry> find(String email) {
        return jpa.findById(email)
                .map(e -> new Entry(e.getCodeHash(), e.getExpiresAt()));
    }

    @Override
    public void remove(String email) {
        jpa.deleteById(email);
    }
}
