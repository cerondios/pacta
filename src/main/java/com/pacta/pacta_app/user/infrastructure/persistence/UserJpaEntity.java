package com.pacta.pacta_app.user.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserJpaEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    private String phoneIndicative;
    private String phoneNumber;
    private String country;
    private String city;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private int score;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role", nullable = false)
    @Builder.Default
    private Set<String> roles = new HashSet<>();

    @Column(nullable = false, updatable = false)
    private String createdAt;

    private String updatedAt;
    private String updatedBy;
    private String removalReason;
    private String removedAt;
    private String removedBy;
}
