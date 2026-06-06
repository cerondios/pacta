package com.pacta.pacta_app.auth.domain;

import java.time.Instant;
import java.util.Optional;

/**
 * Stores one outstanding login/verification code per email. The current binding
 * is in-memory; swap for a TTL-backed store (e.g. Redis) by providing another
 * {@link VerificationCodeStore} bean.
 */
public interface VerificationCodeStore {

    void save(String email, String codeHash, Instant expiresAt);

    Optional<Entry> find(String email);

    void remove(String email);

    /** A stored code (hashed) and its expiry. */
    record Entry(String codeHash, Instant expiresAt) {}
}
