package com.pacta.pacta_app.auth.infrastructure;

import com.pacta.pacta_app.auth.application.EmailSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Development {@link EmailSender} for the {@code local}/{@code default} profiles:
 * logs the code instead of sending mail (no SMTP needed locally). Other profiles
 * use {@link SmtpEmailSender}.
 */
@Slf4j
@Component
@Profile({"local", "default"})
public class LogEmailSender implements EmailSender {

    @Override
    public void sendLoginCode(String email, String code) {
        log.info("[email] login code for {} -> {}", email, code);
    }
}
