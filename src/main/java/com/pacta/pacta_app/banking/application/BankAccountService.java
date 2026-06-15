package com.pacta.pacta_app.banking.application;

import com.pacta.pacta_app.banking.domain.AccountType;
import com.pacta.pacta_app.banking.domain.BankAccount;
import com.pacta.pacta_app.banking.domain.IBankAccountRepository;
import com.pacta.pacta_app.paymentgateway.domain.IPaymentGatewayService;
import com.pacta.pacta_app.shared.domain.DateUtil;
import com.pacta.pacta_app.shared.domain.IdGenerator;
import com.pacta.pacta_app.shared.domain.MetricRecorder;
import com.pacta.pacta_app.user.domain.IUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class BankAccountService {

    private final IBankAccountRepository bankAccounts;
    private final IUserRepository        users;
    private final MetricRecorder        metrics;
    private final IdGenerator           ids;
    private final IPaymentGatewayService gateway;

    /**
     * Validates that {@code bankName} is a real, currently active institution on
     * the payment gateway's bank network before persisting the account. This does
     * NOT verify that the specific account number/holder is correct — the gateway
     * has no public ownership-verification endpoint — it only confirms the bank
     * itself exists and is active in the network Pacta will use to pay landlords out.
     */
    @Transactional
    public BankAccount add(String userId, String bankName, String accountNumber,
                           AccountType accountType, String holderName) {
        requireActiveBank(bankName);
        BankAccount account = BankAccount.create(ids, userId, bankName, mask(accountNumber), accountType, holderName);
        bankAccounts.save(account);
        addScore(userId, 20);
        metrics.incrementCounter("bank.account.added");
        return account;
    }

    private void requireActiveBank(String bankName) {
        boolean recognized = gateway.listAvailableBanks().stream()
                .anyMatch(bank -> normalize(bank.name()).equals(normalize(bankName)));
        if (!recognized) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "'" + bankName + "' no es un banco activo en la red PSE");
        }
    }

    private static String normalize(String value) {
        if (value == null) return "";
        String stripped = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return stripped.toUpperCase(Locale.ROOT).trim();
    }

    @Transactional
    public void remove(String userId, String accountId) {
        BankAccount account = bankAccounts.findById(accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Bank account not found: " + accountId));
        if (!account.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        bankAccounts.deleteById(accountId);
    }

    public List<BankAccount> findByUserId(String userId) {
        return bankAccounts.findByUserId(userId);
    }

    private void addScore(String userId, int points) {
        users.findById(userId).ifPresent(u ->
                users.update(u.addScore(points)));
    }

    private static String mask(String raw) {
        if (raw == null || raw.length() < 4) return raw;
        return "****" + raw.substring(raw.length() - 4);
    }
}
