package com.pacta.pacta_app.paymentgateway.domain;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Port for any external payment gateway capable of direct bank-transfer collections.
 * The rest of the application talks only to this interface — never to a specific provider's
 * client or a specific country's bank-transfer protocol — so the underlying gateway
 * (currently Wompi, using Colombia's PSE rail) can be swapped by adding a new adapter.
 */
public interface IPaymentGatewayService {

    /** Banks currently available for a direct bank-transfer payment. */
    List<GatewayBank> listAvailableBanks();

    /** Starts a bank-transfer payment and returns the gateway transaction, including the bank redirect URL. */
    GatewayTransaction createBankTransferTransaction(GatewayBankTransferRequest request);

    /** Looks up the current state of a previously created transaction. */
    GatewayTransaction getTransaction(String gatewayTransactionId);

    /**
     * Validates and parses an inbound webhook payload from the gateway.
     * Returns empty if the payload is malformed or fails signature verification.
     */
    Optional<GatewayWebhookEvent> parseWebhookEvent(Map<String, Object> payload);
}
