package com.pacta.pacta_app.paymentgateway.domain;

/** Result of validating and parsing an inbound webhook call from the payment gateway. */
public record GatewayWebhookEvent(String gatewayTransactionId, GatewayTransactionStatus status) {}
