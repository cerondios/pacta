package com.pacta.pacta_app.banking.infrastructure;

import com.pacta.pacta_app.banking.domain.BankAccount;
import com.pacta.pacta_app.banking.domain.IBankAccountRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Profile({"local", "default"})
public class InMemoryBankAccountRepository implements IBankAccountRepository {

    private final Map<String, BankAccount> store = new ConcurrentHashMap<>();

    @Override
    public BankAccount save(BankAccount account) {
        store.put(account.getId(), account);
        return account;
    }

    @Override
    public List<BankAccount> findByUserId(String userId) {
        return store.values().stream().filter(a -> a.getUserId().equals(userId)).toList();
    }

    @Override
    public Optional<BankAccount> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public void deleteById(String id) {
        store.remove(id);
    }
}
