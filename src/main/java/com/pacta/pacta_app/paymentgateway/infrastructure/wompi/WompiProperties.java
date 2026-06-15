package com.pacta.pacta_app.paymentgateway.infrastructure.wompi;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "wompi")
public record WompiProperties(
        String baseUrl,
        String publicKey,
        String privateKey,
        String eventsKey,
        String integritySecret
) {}
