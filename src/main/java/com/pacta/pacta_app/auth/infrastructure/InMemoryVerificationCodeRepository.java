package com.pacta.pacta_app.auth.infrastructure;

import com.pacta.pacta_app.auth.domain.IVerificationCodeRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Profile({"local", "default"})
public class InMemoryVerificationCodeRepository implements IVerificationCodeRepository {

    private final Map<String, Entry> store = new ConcurrentHashMap<>();

    @Override
    public void save(String email, String codeHash, String expiresAt) {
        store.put(key(email), new Entry(codeHash, expiresAt));
    }

    @Override
    public Optional<Entry> find(String email) {
        Entry entry = store.get(key(email));
        if (entry != null && Instant.now().isAfter(Instant.parse(entry.expiresAt()))) {
            store.remove(key(email));
            return Optional.empty();
        }
        return Optional.ofNullable(entry);
    }

    @Override
    public void remove(String email) {
        store.remove(key(email));
    }

    private static String key(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }
}
