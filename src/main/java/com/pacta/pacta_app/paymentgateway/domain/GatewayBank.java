package com.pacta.pacta_app.paymentgateway.domain;

/** A bank available for direct bank-transfer payments, as reported by the payment gateway. */
public record GatewayBank(String code, String name) {}
