package com.pacta.pacta_app.payment.application.dto;

import jakarta.validation.constraints.NotBlank;

public record InitiatePaymentRequest(
        @NotBlank String propertyRequestId,
        @NotBlank String bankCode,
        @NotBlank String payerLegalIdType,
        @NotBlank String payerLegalId,
        int payerType,
        @NotBlank String customerEmail,
        @NotBlank String redirectUrl
) {}
