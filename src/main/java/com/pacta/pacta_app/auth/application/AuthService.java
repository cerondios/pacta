package com.pacta.pacta_app.auth.application;

import com.pacta.pacta_app.auth.domain.VerificationCodeStore;
import com.pacta.pacta_app.user.domain.Role;
import com.pacta.pacta_app.user.domain.User;
import com.pacta.pacta_app.user.domain.UserRepository;
import com.pacta.pacta_app.auth.infrastructure.JwtService;
import com.pacta.pacta_app.shared.domain.MetricRecorder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

/**
 * Passwordless auth. Register/login both email a one-time code; {@link #verify}
 * exchanges a valid code for a JWT. Codes are stored hashed, single-use, and
 * expire after a configurable TTL.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final UserRepository users;
    private final VerificationCodeStore codes;
    private final EmailSender emailSender;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwt;
    private final MetricRecorder metrics;

    @Value("${pacta.security.code.ttl-seconds:600}")
    private long codeTtlSeconds;

    /** Creates a new account and emails a login code. */
    public void register(String email, Set<Role> roles) {
        String normalizedEmail = normalize(email);
        if (users.existsByEmail(normalizedEmail)) {
            metrics.incrementCounter("auth.register.conflict");
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }

        users.save(User.builder()
                .id(UUID.randomUUID().toString())
                .email(normalizedEmail)
                .roles(roles)
                .createdAt(Instant.now())
                .build());

        metrics.incrementCounter("auth.register.success");
        issueCode(normalizedEmail);
    }

    /** Emails a login code if the account exists (silent otherwise, to avoid enumeration). */
    public void login(String email) {
        String normalizedEmail = normalize(email);
        if (users.existsByEmail(normalizedEmail)) {
            issueCode(normalizedEmail);
            metrics.incrementCounter("auth.login.code_sent");
        } else {
            metrics.incrementCounter("auth.login.unknown_email");
        }
    }

    /** Validates a one-time code and returns a JWT, or 401 if invalid/expired. */
    public AuthResponse verify(String email, String code) {
        String normalizedEmail = normalize(email);
        VerificationCodeStore.Entry entry = codes.find(normalizedEmail)
                .orElseThrow(() -> unauthorized("no_code"));

        if (Instant.now().isAfter(entry.expiresAt())) {
            codes.remove(normalizedEmail);
            throw unauthorized("expired");
        }
        if (!passwordEncoder.matches(code, entry.codeHash())) {
            throw unauthorized("mismatch");
        }

        codes.remove(normalizedEmail); // single use
        User user = users.findByEmail(normalizedEmail)
                .orElseThrow(() -> unauthorized("no_user"));

        metrics.incrementCounter("auth.verify.success");
        return AuthResponse.bearer(jwt.generate(user), jwt.ttlSeconds());
    }

    private void issueCode(String email) {
        String code = String.format("%06d", RANDOM.nextInt(1_000_000));
        codes.save(email, passwordEncoder.encode(code), Instant.now().plusSeconds(codeTtlSeconds));
        emailSender.sendLoginCode(email, code);
    }

    private ResponseStatusException unauthorized(String reason) {
        metrics.incrementCounter("auth.verify.failure", "reason", reason);
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired code");
    }

    private static String normalize(String email) {
        return email.trim().toLowerCase();
    }
}
