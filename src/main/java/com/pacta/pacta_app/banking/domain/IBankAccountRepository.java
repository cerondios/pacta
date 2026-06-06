package com.pacta.pacta_app.banking.domain;

import java.util.List;
import java.util.Optional;

public interface IBankAccountRepository {
    BankAccount save(BankAccount account);
    List<BankAccount> findByUserId(String userId);
    Optional<BankAccount> findById(String id);
    void deleteById(String id);
}
