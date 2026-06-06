package com.pacta.pacta_app.banking.application.dto;

import com.pacta.pacta_app.banking.domain.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BankAccountRequest(
        @NotBlank String bankName,
        @NotBlank String accountNumber,
        @NotNull AccountType accountType,
        @NotBlank String holderName
) {}
