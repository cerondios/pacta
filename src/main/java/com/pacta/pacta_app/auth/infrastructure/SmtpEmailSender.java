package com.pacta.pacta_app.auth.infrastructure;

import com.pacta.pacta_app.auth.application.EmailSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * Sends one-time login codes over SMTP via Spring's {@link JavaMailSender}.
 * Active in every profile except {@code local}/{@code default} (those use
 * {@link LogEmailSender}). Requires {@code spring.mail.*} to be configured.
 */
@Slf4j
@Component
@Profile({"dev", "prod"})
@RequiredArgsConstructor
public class SmtpEmailSender implements EmailSender {

    private final JavaMailSender mailSender;

    @Value("${pacta.mail.from:no-reply@pacta.app}")
    private String from;

    @Override
    public void sendLoginCode(String email, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(email);
        message.setSubject("Your Pacta login code");
        message.setText("""
                Your one-time login code is: %s

                It expires shortly. If you didn't request it, you can ignore this email.""".formatted(code));
        mailSender.send(message);
        log.debug("Login code email sent to {}", email);
    }
}
