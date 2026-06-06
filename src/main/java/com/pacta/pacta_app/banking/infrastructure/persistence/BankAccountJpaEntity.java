package com.pacta.pacta_app.banking.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "bank_accounts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BankAccountJpaEntity {
    @Id private String id;
    @Column(nullable = false) private String userId;
    @Column(nullable = false) private String bankName;
    @Column(nullable = false) private String accountNumber;
    @Column(nullable = false) private String accountType;
    @Column(nullable = false) private String holderName;
    @Column(nullable = false) private String createdAt;
}
