package com.pacta.pacta_app.banking.domain;

import com.pacta.pacta_app.shared.domain.DateUtil;
import com.pacta.pacta_app.shared.domain.IdGenerator;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BankAccount {

    private final String      id;
    private final String      userId;
    private final String      bankName;
    private final String      accountNumber;
    private final AccountType accountType;
    private final String      holderName;
    private final String      createdAt;

    public static BankAccount create(IdGenerator ids, String userId, String bankName,
                                     String accountNumber, AccountType accountType, String holderName) {
        return BankAccount.builder()
                .id(ids.generate())
                .userId(userId)
                .bankName(bankName)
                .accountNumber(accountNumber)
                .accountType(accountType)
                .holderName(holderName)
                .createdAt(DateUtil.now())
                .build();
    }
}
