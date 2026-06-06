package com.pacta.pacta_app.auth.application;

/**
 * Delivers transactional emails. The current binding ({@code LogEmailSender})
 * just logs the code for local development; provide a real SMTP/SES-backed
 * {@link EmailSender} bean for other environments.
 */
public interface EmailSender {

    void sendLoginCode(String email, String code);
}
