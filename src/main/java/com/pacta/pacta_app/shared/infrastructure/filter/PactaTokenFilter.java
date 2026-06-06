package com.pacta.pacta_app.shared.infrastructure.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PactaTokenFilter extends OncePerRequestFilter {

    public static final String HEADER_USER_ID    = "X-User-Id";
    public static final String HEADER_USER_EMAIL = "X-User-Email";
    public static final String HEADER_USER_ROLES = "X-User-Roles";

    private static final String TOKEN_HEADER = "X-Pacta-Token";

    private final JwtDecoder jwtDecoder;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.equals("/api/ping") || path.startsWith("/api/auth/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String token = request.getHeader(TOKEN_HEADER);

        if (token == null || token.isBlank()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing " + TOKEN_HEADER);
            return;
        }

        try {
            var jwt = jwtDecoder.decode(token);
            var wrapped = new MutableHttpServletRequest(request);
            wrapped.addHeader(HEADER_USER_ID,    jwt.getClaimAsString("id"));
            wrapped.addHeader(HEADER_USER_EMAIL, jwt.getSubject());
            List<String> roles = jwt.getClaim("roles");
            wrapped.addHeader(HEADER_USER_ROLES, roles != null ? String.join(",", roles) : "");
            chain.doFilter(wrapped, response);
        } catch (JwtException e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
        }
    }

}
