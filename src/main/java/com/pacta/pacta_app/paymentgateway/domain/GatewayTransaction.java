package com.pacta.pacta_app.paymentgateway.domain;

/** Gateway-agnostic view of a transaction, returned by the gateway right after creation or on lookup. */
public record GatewayTransaction(
        String gatewayTransactionId,
        GatewayTransactionStatus status,
        String redirectUrl
) {}
