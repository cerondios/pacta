package com.pacta.pacta_app.paymentgateway.infrastructure.wompi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

/**
 * Thin wrapper over the public Wompi API (https://docs.wompi.co/docs/colombia/api-referencia).
 * All calls hit {@code wompi.base-url} (sandbox by default) with the configured public/private keys.
 *
 * Builds its own {@link RestClient} rather than relying on a Boot-autoconfigured
 * {@code RestClient.Builder} (not provided in this Boot 4 module set), but still
 * reuses the app's configured {@link JsonMapper} (Jackson 3) so the snake_case
 * mapping that happens to match Wompi's own JSON contract stays consistent.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WompiClient {

    private final WompiProperties properties;
    private final JsonMapper      jsonMapper;

    private RestClient client() {
        return RestClient.builder()
                .baseUrl(properties.baseUrl())
                .configureMessageConverters(builder -> builder.withJsonConverter(new JacksonJsonHttpMessageConverter(jsonMapper)))
                .build();
    }

    /** Acceptance token required on every transaction — pulled from the merchant's own config. */
    public String getAcceptanceToken() {
        try {
            var res = client().get()
                    .uri("/merchants/{publicKey}", properties.publicKey())
                    .retrieve()
                    .body(MerchantEnvelope.class);
            return res.data().presignedAcceptance().acceptanceToken();
        } catch (Exception e) {
            log.error("Failed to fetch Wompi acceptance token", e);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Wompi unavailable");
        }
    }

    /** The live list of banks participating in the PSE network — the validity check for a bank account's bank. */
    public List<WompiDtos.FinancialInstitution> listPseFinancialInstitutions() {
        try {
            var res = client().get()
                    .uri("/pse/financial_institutions")
                    .header("Authorization", "Bearer " + properties.publicKey())
                    .retrieve()
                    .body(WompiDtos.FinancialInstitutionsList.class);
            return res != null ? res.data() : List.of();
        } catch (Exception e) {
            log.error("Failed to fetch Wompi PSE financial institutions", e);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Wompi unavailable");
        }
    }

    public WompiDtos.TransactionData createPseTransaction(WompiDtos.CreateTransactionRequest req) {
        try {
            var res = client().post()
                    .uri("/transactions")
                    .header("Authorization", "Bearer " + properties.privateKey())
                    .body(req)
                    .retrieve()
                    .body(TransactionEnvelope.class);
            return res.data();
        } catch (Exception e) {
            log.error("Failed to create Wompi PSE transaction", e);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Could not start PSE payment");
        }
    }

    public WompiDtos.TransactionData getTransaction(String transactionId) {
        try {
            var res = client().get()
                    .uri("/transactions/{id}", transactionId)
                    .header("Authorization", "Bearer " + properties.privateKey())
                    .retrieve()
                    .body(TransactionEnvelope.class);
            return res.data();
        } catch (Exception e) {
            log.error("Failed to fetch Wompi transaction {}", transactionId, e);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Wompi unavailable");
        }
    }

    private record MerchantEnvelope(WompiDtos.MerchantData data) {}
    private record TransactionEnvelope(WompiDtos.TransactionData data) {}
}
