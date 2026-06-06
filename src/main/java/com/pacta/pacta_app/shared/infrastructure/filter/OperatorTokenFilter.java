package com.pacta.pacta_app.shared.infrastructure.filter;

import com.pacta.pacta_app.admin.infrastructure.AdminJwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Validates operator JWTs for admin-only routes.
 * Rejects user tokens by checking for the presence of the adminRole claim.
 * Injects X-Operator-Id and X-Operator-Role headers for downstream use.
 *
 * Protected paths: /api/admins/**, /api/reviewer/**
 * Registered in SecurityConfig — not a global Spring-managed filter.
 */
@RequiredArgsConstructor
public class OperatorTokenFilter extends OncePerRequestFilter {

    public static final String HEADER_OPERATOR_ID   = "X-Operator-Id";
    public static final String HEADER_OPERATOR_ROLE = "X-Operator-Role";

    private static final String TOKEN_HEADER = "X-Operator-Token";

    private final JwtDecoder jwtDecoder;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String token = request.getHeader(TOKEN_HEADER);

        if (token == null || token.isBlank()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing X-Operator-Token");
            return;
        }

        try {
            var jwt = jwtDecoder.decode(token);

            // Reject user tokens — operator JWTs always carry adminRole, user JWTs never do
            if (jwt.getClaimAsString(AdminJwtService.CLAIM_ADMIN_ROLE) == null) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Operator token required");
                return;
            }

            var wrapped = new MutableHttpServletRequest(request);
            wrapped.addHeader(HEADER_OPERATOR_ID,   jwt.getClaimAsString(AdminJwtService.CLAIM_ADMIN_ID));
            wrapped.addHeader(HEADER_OPERATOR_ROLE, jwt.getClaimAsString(AdminJwtService.CLAIM_ADMIN_ROLE));
            chain.doFilter(wrapped, response);

        } catch (JwtException e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
        }
    }
}
