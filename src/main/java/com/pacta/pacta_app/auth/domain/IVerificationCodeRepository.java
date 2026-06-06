package com.pacta.pacta_app.auth.domain;

import java.time.Instant;
import java.util.Optional;

public interface IVerificationCodeRepository {
    void save(String email, String codeHash, String expiresAt);
    Optional<Entry> find(String email);
    void remove(String email);

    record Entry(String codeHash, String expiresAt) {}
}
