package com.pacta.pacta_app.paymentgateway.infrastructure.wompi;

import java.util.List;
import java.util.Map;

/** DTOs mirroring Wompi's public API contract (https://docs.wompi.co). */
public class WompiDtos {

    public record MerchantData(PresignedAcceptance presignedAcceptance) {}

    public record PresignedAcceptance(String acceptanceToken, String permalink) {}

    public record FinancialInstitution(
            String financialInstitutionCode,
            String financialInstitutionName
    ) {}

    public record PsePaymentMethod(
            String type,
            int userType,
            String userLegalIdType,
            String userLegalId,
            String financialInstitutionCode,
            String paymentDescription
    ) {}

    public record CreateTransactionRequest(
            long amountInCents,
            String currency,
            String customerEmail,
            PsePaymentMethod paymentMethod,
            String reference,
            String acceptanceToken,
            String redirectUrl,
            /** Integrity signature: SHA256(reference + amountInCents + currency + integritySecret). */
            String signature
    ) {}

    public record TransactionData(
            String id,
            String createdAt,
            long amountInCents,
            String reference,
            String currency,
            String status,
            String statusMessage,
            Map<String, Object> paymentMethod
    ) {
        /** PSE transactions carry the bank redirect URL under payment_method.extra.async_payment_url. */
        public String asyncPaymentUrl() {
            if (paymentMethod == null) return null;
            Object extra = paymentMethod.get("extra");
            if (!(extra instanceof Map<?, ?> extraMap)) return null;
            Object url = extraMap.get("async_payment_url");
            return url != null ? url.toString() : null;
        }
    }

    public record FinancialInstitutionsList(List<FinancialInstitution> data) {}
}
