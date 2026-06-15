package com.pacta.pacta_app.paymentgateway.domain;

/**
 * Gateway-agnostic request to collect a payment by direct bank account transfer
 * (the payer authorizes the debit from their own bank, as opposed to a card).
 */
public record GatewayBankTransferRequest(
        long amountInCents,
        String currency,
        String customerEmail,
        String reference,
        String redirectUrl,
        String payerLegalIdType,
        String payerLegalId,
        int payerType,
        String bankCode,
        String paymentDescription
) {}
