package com.pacta.pacta_app.banking.infrastructure.persistence;

import com.pacta.pacta_app.banking.domain.AccountType;
import com.pacta.pacta_app.banking.domain.BankAccount;
import com.pacta.pacta_app.banking.domain.IBankAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile({"dev", "prod"})
@RequiredArgsConstructor
public class PostgresBankAccountRepository implements IBankAccountRepository {

    private final BankAccountSpringRepository jpa;

    @Override public BankAccount save(BankAccount a) { jpa.save(toEntity(a)); return a; }
    @Override public List<BankAccount> findByUserId(String userId) { return jpa.findByUserId(userId).stream().map(this::toDomain).toList(); }
    @Override public Optional<BankAccount> findById(String id) { return jpa.findById(id).map(this::toDomain); }
    @Override public void deleteById(String id) { jpa.deleteById(id); }

    private BankAccountJpaEntity toEntity(BankAccount a) {
        return BankAccountJpaEntity.builder().id(a.getId()).userId(a.getUserId())
                .bankName(a.getBankName()).accountNumber(a.getAccountNumber())
                .accountType(a.getAccountType().name()).holderName(a.getHolderName())
                .createdAt(a.getCreatedAt()).build();
    }

    private BankAccount toDomain(BankAccountJpaEntity e) {
        return BankAccount.builder().id(e.getId()).userId(e.getUserId())
                .bankName(e.getBankName()).accountNumber(e.getAccountNumber())
                .accountType(AccountType.valueOf(e.getAccountType())).holderName(e.getHolderName())
                .createdAt(e.getCreatedAt()).build();
    }
}
