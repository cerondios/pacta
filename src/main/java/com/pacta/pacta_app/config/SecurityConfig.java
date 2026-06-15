package com.pacta.pacta_app.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.proc.SecurityContext;
import com.pacta.pacta_app.shared.infrastructure.filter.FilterByRouteMatcher;
import com.pacta.pacta_app.shared.infrastructure.filter.OperatorTokenFilter;
import com.pacta.pacta_app.shared.infrastructure.filter.PactaTokenFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
public class SecurityConfig {

    @Value("${pacta.security.jwt.secret}")
    private String jwtSecret;

    /**
     * Route ownership — single source of truth:
     *
     *   /api/auth/**                  → public (no token required)
     *   /api/ping                     → public (no token required)
     *   /api/payments/webhook         → public (Wompi calls this; verified via checksum)
     *   /api/admin/**                 → OperatorTokenFilter
     *   /api/property-configs/**      → OperatorTokenFilter (admin writes) / public (GET)
     *   everything else               → PactaTokenFilter
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        JwtDecoder decoder = jwtDecoder();

        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            .addFilterBefore(
                new FilterByRouteMatcher(
                    req -> {
                        String uri    = req.getRequestURI();
                        String method = req.getMethod();
                        return isAdminRoute(uri) || isPropertyConfigAdminRoute(uri, method) || isFilesViewRoute(uri);
                    },
                    new OperatorTokenFilter(decoder)
                ), BearerTokenAuthenticationFilter.class
            )
            .addFilterBefore(
                new FilterByRouteMatcher(
                    req -> {
                        String uri    = req.getRequestURI();
                        String method = req.getMethod();
                        return !uri.startsWith("/api/auth/")
                            && !uri.equals("/api/ping")
                            && !uri.equals("/api/payments/webhook")
                            && !isAdminRoute(uri)
                            && !isPropertyConfigAdminRoute(uri, method)
                            && !isFilesViewRoute(uri);
                    },
                    new PactaTokenFilter(decoder)
                ), BearerTokenAuthenticationFilter.class
            );

        return http.build();
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<SecurityContext>(secretKey()));
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withSecretKey(secretKey()).macAlgorithm(MacAlgorithm.HS256).build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("X-Pacta-Token", "X-Operator-Token"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private static boolean isAdminRoute(String uri) {
        return uri.equals("/api/admin") || uri.startsWith("/api/admin/");
    }

    private static boolean isPropertyConfigAdminRoute(String uri, String method) {
        // GET /api/property-configs               → public (landlord form)
        // GET /api/property-configs/all           → admin
        // POST /api/property-configs              → admin
        // PATCH|DELETE /api/property-configs/{id} → admin
        if (uri.equals("/api/property-configs/all")) return true;
        if (uri.equals("/api/property-configs"))     return !method.equals("GET");
        if (uri.startsWith("/api/property-configs/")) return true;
        return false;
    }

    private static boolean isFilesViewRoute(String uri) {
        return uri.equals("/api/files/url");
    }

    private SecretKey secretKey() {
        return new SecretKeySpec(jwtSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }
}
