package com.pacta.pacta_app.user.infrastructure.persistence;

import com.pacta.pacta_app.user.domain.*;
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
public class PostgresUserRepository implements IUserRepository {

    private final UserJpaRepository jpa;

    @Override
    public User save(User user) {
        jpa.save(toEntity(user));
        return user;
    }

    @Override
    public User update(User user) {
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

    @Override
    public List<User> findAllByRole(Role role) {
        return jpa.findAllByRole(role.name()).stream().map(this::toDomain).toList();
    }

    // ── Mappers ───────────────────────────────────────────────────────────────

    private UserJpaEntity toEntity(User u) {
        UserJpaEntity e = UserJpaEntity.builder()
                .id(u.getId())
                .fullName(u.getFullName())
                .email(u.getEmail())
                .phoneIndicative(u.getPhone() != null ? u.getPhone().indicative() : null)
                .phoneNumber(u.getPhone() != null ? u.getPhone().number() : null)
                .country(u.getCountry())
                .city(u.getCity())
                .status(u.getStatus().name())
                .score(u.getScore())
                .createdAt(u.getCreatedAt())
                .updatedAt(u.getUpdatedAt())
                .updatedBy(u.getUpdatedBy())
                .removalReason(u.getRemovalReason())
                .removedAt(u.getRemovedAt())
                .removedBy(u.getRemovedBy())
                .build();
        u.getRoles().forEach(r -> e.getRoles().add(r.name()));
        return e;
    }

    private User toDomain(UserJpaEntity e) {
        Phone phone = (e.getPhoneIndicative() != null)
                ? new Phone(e.getPhoneIndicative(), e.getPhoneNumber())
                : null;

        User.UserBuilder b = User.builder()
                .id(e.getId())
                .fullName(e.getFullName())
                .email(e.getEmail())
                .phone(phone)
                .country(e.getCountry())
                .city(e.getCity())
                .status(UserStatus.valueOf(e.getStatus()))
                .score(e.getScore())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .updatedBy(e.getUpdatedBy())
                .removalReason(e.getRemovalReason())
                .removedAt(e.getRemovedAt())
                .removedBy(e.getRemovedBy());
        if (e.getRoles() != null) {
            e.getRoles().forEach(r -> b.role(Role.valueOf(r)));
        }
        return b.build();
    }
}
