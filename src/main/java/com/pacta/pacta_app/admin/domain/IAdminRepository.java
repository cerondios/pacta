package com.pacta.pacta_app.admin.domain;

import java.util.List;
import java.util.Optional;

public interface IAdminRepository {
    Admin save(Admin admin);
    Admin update(Admin admin);
    Optional<Admin> findById(String id);
    Optional<Admin> findByEmail(String email);
    boolean existsByEmail(String email);
    List<Admin> findAll();
    List<Admin> findAllByRole(AdminRole role);
}
