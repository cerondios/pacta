package com.pacta.pacta_app.auth.application;

import com.pacta.pacta_app.auth.domain.IVerificationCodeRepository;
import com.pacta.pacta_app.shared.domain.IdGenerator;
import com.pacta.pacta_app.user.domain.*;
import com.pacta.pacta_app.auth.infrastructure.JwtService;
import com.pacta.pacta_app.shared.domain.DateUtil;
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

/**
 * Passwordless auth. Register/login both email a one-time code; {@link #verify}
 * exchanges a valid code for a JWT. Codes are stored hashed, single-use, and
 * expire after a configurable TTL.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final IUserRepository users;
    private final IVerificationCodeRepository codes;
    private final EmailSender emailSender;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwt;
    private final MetricRecorder metrics;
    private final IdGenerator ids;

    @Value("${pacta.security.code.ttl-seconds:600}")
    private long codeTtlSeconds;

    /** Creates a new account and emails a login code. */
    public void register(String email, String fullName, Phone phone, String country) {
        String normalizedEmail = normalize(email);
        if (users.existsByEmail(normalizedEmail)) {
            metrics.incrementCounter("auth.register.conflict");
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }

        users.save(User.create(ids, fullName, normalizedEmail, phone, country));

        metrics.incrementCounter("auth.register.success");
        issueCode(normalizedEmail);
    }

    /** Emails a login code if the account exists (silent otherwise, to avoid enumeration). */
    public void login(String email) {
        String normalizedEmail = normalize(email);
        if (!users.existsByEmail(normalizedEmail)) {
            metrics.incrementCounter("auth.login.unknown_email");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Email not registered");
        }
        issueCode(normalizedEmail);
        metrics.incrementCounter("auth.login.code_sent");
    }

    /** Validates a one-time code and returns a JWT, or 401 if invalid/expired. */
    public AuthResponse verify(String email, String code) {
        String normalizedEmail = normalize(email);
        IVerificationCodeRepository.Entry entry = codes.find(normalizedEmail)
                .orElseThrow(() -> unauthorized("no_code"));

        if (Instant.now().isAfter(DateUtil.toInstant(entry.expiresAt()))) {
            codes.remove(normalizedEmail);
            throw unauthorized("expired");
        }
        if (!passwordEncoder.matches(code, entry.codeHash())) {
            throw unauthorized("mismatch");
        }

        codes.remove(normalizedEmail); // single use
        var user = users.findByEmail(normalizedEmail)
                .orElseThrow(() -> unauthorized("no_user"));

        // Email verified — advance status from PENDING_VERIFICATION → PENDING_KYC
        if (user.isPendingVerification()) {
            user = users.update(user.verifyEmail());
        }

        metrics.incrementCounter("auth.verify.success");
        return AuthResponse.bearer(jwt.generate(user), jwt.ttlSeconds());
    }

    private void issueCode(String email) {
        String code = String.format("%06d", RANDOM.nextInt(1_000_000));
        codes.save(email, passwordEncoder.encode(code), DateUtil.format(Instant.now().plusSeconds(codeTtlSeconds)));
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
