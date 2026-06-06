package com.pacta.pacta_app.admin.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "operators")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminJpaEntity {
    @Id private String id;
    @Column(nullable = false) private String fullName;
    @Column(nullable = false, unique = true) private String email;
    @Column(nullable = false) private String role;
    @Column(nullable = false) private String status;
    @Column private String passwordHash;
    @Column(nullable = false) private String createdAt;
    @Column private String updatedAt;
}
