package com.pacta.pacta_app.auth.infrastructure;

import com.pacta.pacta_app.auth.domain.VerificationCodeStore;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * In-memory {@link VerificationCodeStore}, keyed by normalized email. State is
 * lost on restart and expired entries are pruned lazily on read.
 */
@Profile({"local", "default"})
@Repository
public class InMemoryVerificationCodeStore implements VerificationCodeStore {

    private final ConcurrentMap<String, Entry> codesByEmail = new ConcurrentHashMap<>();

    @Override
    public void save(String email, String codeHash, Instant expiresAt) {
        codesByEmail.put(key(email), new Entry(codeHash, expiresAt));
    }

    @Override
    public Optional<Entry> find(String email) {
        Entry entry = codesByEmail.get(key(email));
        if (entry != null && Instant.now().isAfter(entry.expiresAt())) {
            codesByEmail.remove(key(email));
            return Optional.empty();
        }
        return Optional.ofNullable(entry);
    }

    @Override
    public void remove(String email) {
        codesByEmail.remove(key(email));
    }

    private static String key(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }
}
