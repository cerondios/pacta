package com.pacta.pacta_app.admin.infrastructure;

import com.pacta.pacta_app.admin.domain.Admin;
import com.pacta.pacta_app.admin.domain.AdminRole;
import com.pacta.pacta_app.admin.domain.IAdminRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Profile({"local", "default"})
public class InMemoryAdminRepository implements IAdminRepository {

    private final Map<String, Admin> store = new ConcurrentHashMap<>();

    @Override
    public Admin save(Admin admin) {
        store.put(admin.getId(), admin);
        return admin;
    }

    @Override
    public Admin update(Admin admin) {
        store.put(admin.getId(), admin);
        return admin;
    }

    @Override
    public Optional<Admin> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<Admin> findByEmail(String email) {
        return store.values().stream()
                .filter(a -> a.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    @Override
    public boolean existsByEmail(String email) {
        return store.values().stream().anyMatch(a -> a.getEmail().equalsIgnoreCase(email));
    }

    @Override
    public List<Admin> findAll() {
        return List.copyOf(store.values());
    }

    @Override
    public List<Admin> findAllByRole(AdminRole role) {
        return store.values().stream().filter(a -> a.getRole() == role).toList();
    }
}
