package com.pacta.pacta_app.banking.application.dto;

import com.pacta.pacta_app.banking.domain.BankAccount;

public record BankAccountResponse(
        String id, String bankName, String accountNumber,
        String accountType, String holderName
) {
    public static BankAccountResponse from(BankAccount a) {
        return new BankAccountResponse(a.getId(), a.getBankName(),
                a.getAccountNumber(), a.getAccountType().name(), a.getHolderName());
    }
}
