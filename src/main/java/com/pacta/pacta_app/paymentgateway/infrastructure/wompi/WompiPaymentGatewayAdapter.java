package com.pacta.pacta_app.paymentgateway.infrastructure.wompi;

import com.pacta.pacta_app.paymentgateway.domain.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Adapts the Wompi-specific {@link WompiClient} to the gateway-agnostic
 * {@link IPaymentGatewayService} port. This is the only class in the application
 * that should know Wompi's request/response shapes and webhook signature scheme —
 * everything else depends on the port, so swapping providers means adding a new
 * adapter here rather than touching the rest of the app.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WompiPaymentGatewayAdapter implements IPaymentGatewayService {

    private final WompiClient     wompiClient;
    private final WompiProperties properties;

    @Override
    public List<GatewayBank> listAvailableBanks() {
        return wompiClient.listPseFinancialInstitutions().stream()
                .map(b -> new GatewayBank(b.financialInstitutionCode(), b.financialInstitutionName()))
                .toList();
    }

    @Override
    public GatewayTransaction createBankTransferTransaction(GatewayBankTransferRequest request) {
        String acceptanceToken = wompiClient.getAcceptanceToken();

        // Wompi happens to implement bank transfers via Colombia's PSE rail — that mapping is this adapter's job alone.
        var paymentMethod = new WompiDtos.PsePaymentMethod(
                "PSE", request.payerType(), request.payerLegalIdType(), request.payerLegalId(),
                request.bankCode(), request.paymentDescription());

        String signature = integritySignature(request.reference(), request.amountInCents(), request.currency());

        var txRequest = new WompiDtos.CreateTransactionRequest(
                request.amountInCents(), request.currency(), request.customerEmail(),
                paymentMethod, request.reference(), acceptanceToken, request.redirectUrl(), signature);

        WompiDtos.TransactionData tx = wompiClient.createPseTransaction(txRequest);
        return toGatewayTransaction(tx);
    }

    /** Wompi's required transaction integrity signature: SHA256(reference + amountInCents + currency + integritySecret). */
    private String integritySignature(String reference, long amountInCents, String currency) {
        return sha256(reference + amountInCents + currency + properties.integritySecret());
    }

    @Override
    public GatewayTransaction getTransaction(String gatewayTransactionId) {
        return toGatewayTransaction(wompiClient.getTransaction(gatewayTransactionId));
    }

    /**
     * Wompi event payload: { event, data: { transaction: {...} }, timestamp,
     * signature: { properties: ["transaction.id", "transaction.status", ...], checksum } }.
     * Checksum = SHA256(concat(values of `properties`, in order) + timestamp + events key).
     */
    @Override
    @SuppressWarnings("unchecked")
    public Optional<GatewayWebhookEvent> parseWebhookEvent(Map<String, Object> payload) {
        Map<String, Object> data = (Map<String, Object>) payload.get("data");
        Map<String, Object> signature = (Map<String, Object>) payload.get("signature");
        if (data == null || signature == null) {
            log.warn("Wompi webhook payload missing data/signature");
            return Optional.empty();
        }

        List<String> orderedProperties = (List<String>) signature.get("properties");
        String expectedChecksum = sha256(
                orderedProperties.stream().map(path -> String.valueOf(resolvePath(data, path))).reduce("", String::concat)
                        + payload.get("timestamp") + properties.eventsKey());

        String receivedChecksum = String.valueOf(signature.get("checksum"));
        if (!expectedChecksum.equalsIgnoreCase(receivedChecksum)) {
            log.warn("Wompi webhook checksum mismatch — ignoring event");
            return Optional.empty();
        }

        String transactionId = String.valueOf(resolvePath(data, "transaction.id"));
        String wompiStatus    = String.valueOf(resolvePath(data, "transaction.status"));
        return Optional.of(new GatewayWebhookEvent(transactionId, GatewayTransactionStatus.valueOf(wompiStatus)));
    }

    private static GatewayTransaction toGatewayTransaction(WompiDtos.TransactionData tx) {
        return new GatewayTransaction(
                tx.id(), GatewayTransactionStatus.valueOf(tx.status()), tx.asyncPaymentUrl());
    }

    private static Object resolvePath(Map<String, Object> root, String dottedPath) {
        Object current = root;
        for (String part : dottedPath.split("\\.")) {
            if (!(current instanceof Map<?, ?> map)) return null;
            current = map.get(part);
        }
        return current;
    }

    private static String sha256(String input) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
