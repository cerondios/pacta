package com.pacta.pacta_app.auth.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "verification_codes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerificationCodeJpaEntity {

    @Id
    private String email;

    @Column(nullable = false)
    private String codeHash;

    @Column(nullable = false)
    private String expiresAt;
}
