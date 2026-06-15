package com.pacta.pacta_app.user.infrastructure;

import com.pacta.pacta_app.user.domain.IUserRepository;
import com.pacta.pacta_app.user.domain.Role;
import com.pacta.pacta_app.user.domain.User;
import com.pacta.pacta_app.user.domain.UserStatus;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Profile({"local", "default"})
@Repository
public class InMemoryUserRepository implements IUserRepository {

    private final ConcurrentMap<String, User> store = new ConcurrentHashMap<>();

    @Override
    public User save(User user) {
        store.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return store.values().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    @Override
    public boolean existsByEmail(String email) {
        return store.values().stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(email));
    }

    @Override
    public User update(User user) {
        store.put(user.getId(), user);
        return user;
    }

    @Override
    public List<User> findAll() {
        return List.copyOf(store.values());
    }

    @Override
    public List<User> findAllByRole(Role role) {
        return store.values().stream().filter(u -> u.hasRole(role)).toList();
    }

    @Override
    public List<User> search(String name, String status, String role) {
        return store.values().stream()
                .filter(u -> name   == null || u.getFullName().toLowerCase().contains(name.toLowerCase()))
                .filter(u -> status == null || u.getStatus().name().equals(status))
                .filter(u -> role   == null || u.getRoles().stream().anyMatch(r -> r.name().equals(role)))
                .toList();
    }

    @Override
    public List<User> findByCountryAndStatus(String country, UserStatus status) {
        return store.values().stream()
                .filter(u -> status.equals(u.getStatus()))
                .filter(u -> country == null ? u.getCountry() == null : country.equals(u.getCountry()))
                .toList();
    }
}
