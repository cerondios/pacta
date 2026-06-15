package com.pacta.pacta_app.payment.domain;

import com.pacta.pacta_app.shared.domain.DateUtil;
import com.pacta.pacta_app.shared.domain.IdGenerator;
import lombok.Builder;
import lombok.Getter;

/** A tenant-to-Pacta rent payment, collected via PSE through the configured payment gateway. */
@Getter
@Builder(toBuilder = true)
public class Payment {

    private final String        id;
    private final String        tenantId;
    private final String        propertyRequestId;
    private final long          amountInCents;
    private final String        currency;
    private final PaymentStatus status;
    private final String        reference;
    private final String        gatewayTransactionId;
    private final String        redirectUrl;
    private final String        createdAt;
    private final String        updatedAt;

    public static Payment create(IdGenerator ids, String tenantId, String propertyRequestId,
                                 long amountInCents, String currency, String reference,
                                 String gatewayTransactionId, String redirectUrl) {
        return Payment.builder()
                .id(ids.generate())
                .tenantId(tenantId)
                .propertyRequestId(propertyRequestId)
                .amountInCents(amountInCents)
                .currency(currency)
                .status(PaymentStatus.PENDING)
                .reference(reference)
                .gatewayTransactionId(gatewayTransactionId)
                .redirectUrl(redirectUrl)
                .createdAt(DateUtil.now())
                .build();
    }

    public Payment withStatus(PaymentStatus newStatus) {
        return toBuilder().status(newStatus).updatedAt(DateUtil.now()).build();
    }
}
