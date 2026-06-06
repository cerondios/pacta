package com.pacta.pacta_app.admin.infrastructure;

import com.pacta.pacta_app.admin.domain.Admin;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Issues signed (HMAC-SHA256) JWTs for authenticated operators.
 * Claims:
 *   sub        → operator email
 *   aid        → operator id
 *   role  → role name (REVIEWER | ADMIN | SUPER_ADMIN)
 */
@Service
@RequiredArgsConstructor
public class AdminJwtService {

    public static final String CLAIM_ADMIN_ID   = "id";
    public static final String CLAIM_ADMIN_ROLE = "role";

    private final JwtEncoder jwtEncoder;

    @Value("${pacta.security.jwt.ttl-seconds:3600}")
    private long ttlSeconds;

    public String generate(Admin admin) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("pacta-app")
                .issuedAt(now)
                .expiresAt(now.plus(ttlSeconds, ChronoUnit.SECONDS))
                .subject(admin.getEmail())
                .claim(CLAIM_ADMIN_ID,   admin.getId())
                .claim(CLAIM_ADMIN_ROLE, admin.getRole().name())
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    public long ttlSeconds() {
        return ttlSeconds;
    }
}
