package com.pacta.pacta_app.payment.application.dto;

import com.pacta.pacta_app.payment.domain.Payment;

public record PaymentResponse(
        String id,
        String propertyRequestId,
        long   amountInCents,
        String currency,
        String status,
        String redirectUrl,
        String createdAt
) {
    public static PaymentResponse from(Payment p) {
        return new PaymentResponse(
                p.getId(), p.getPropertyRequestId(), p.getAmountInCents(), p.getCurrency(),
                p.getStatus().name(), p.getRedirectUrl(), p.getCreatedAt());
    }
}
