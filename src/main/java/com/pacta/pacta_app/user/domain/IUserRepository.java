package com.pacta.pacta_app.user.domain;

import java.util.List;
import java.util.Optional;

public interface IUserRepository {
    User save(User user);
    User update(User user);
    Optional<User> findById(String id);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findAll();
    List<User> findAllByRole(Role role);
    List<User> search(String name, String status, String role);
    List<User> findByCountryAndStatus(String country, UserStatus status);
}
