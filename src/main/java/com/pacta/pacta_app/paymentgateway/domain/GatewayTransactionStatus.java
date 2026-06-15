package com.pacta.pacta_app.paymentgateway.domain;

/** Gateway-agnostic transaction status. Adapters map their provider's own status values into this enum. */
public enum GatewayTransactionStatus {
    PENDING,
    APPROVED,
    DECLINED,
    ERROR,
    VOIDED
}
