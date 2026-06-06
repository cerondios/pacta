package com.pacta.pacta_app.user.infrastructure;

import com.pacta.pacta_app.user.domain.User;
import com.pacta.pacta_app.user.domain.UserRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Profile({"local", "default"})
@Repository
public class InMemoryUserRepository implements UserRepository {

    private final ConcurrentMap<String, User> usersByEmail = new ConcurrentHashMap<>();

    @Override
    public User save(User user) {
        usersByEmail.put(key(user.getEmail()), user);
        return user;
    }

    @Override
    public Optional<User> findById(String id) {
        return usersByEmail.values().stream().filter(u -> u.getId().equals(id)).findFirst();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(usersByEmail.get(key(email)));
    }

    @Override
    public boolean existsByEmail(String email) {
        return usersByEmail.containsKey(key(email));
    }

    @Override
    public List<User> findAll() {
        return List.copyOf(usersByEmail.values());
    }

    private static String key(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }
}
