package com.pacta.pacta_app.user.infrastructure.persistence;

import com.pacta.pacta_app.user.domain.Role;
import com.pacta.pacta_app.user.domain.User;
import com.pacta.pacta_app.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Primary
@Profile({"dev", "prod"})
@RequiredArgsConstructor
public class PostgresUserRepository implements UserRepository {

    private final UserJpaRepository jpa;

    @Override
    public User save(User user) {
        jpa.save(toEntity(user));
        return user;
    }

    @Override
    public Optional<User> findById(String id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpa.findByEmail(email).map(this::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpa.existsByEmail(email);
    }

    @Override
    public List<User> findAll() {
        return jpa.findAll().stream().map(this::toDomain).toList();
    }

    private UserJpaEntity toEntity(User user) {
        UserJpaEntity entity = UserJpaEntity.builder()
                .id(user.getId())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .build();
        user.getRoles().forEach(r -> entity.getRoles().add(r.name()));
        return entity;
    }

    private User toDomain(UserJpaEntity entity) {
        User.UserBuilder builder = User.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .createdAt(entity.getCreatedAt());
        entity.getRoles().forEach(r -> builder.role(Role.valueOf(r)));
        return builder.build();
    }
}
