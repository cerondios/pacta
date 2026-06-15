package com.pacta.pacta_app.shared.domain;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/** Role validation utility for service-layer enforcement. */
public final class RoleGuard {

    private RoleGuard() {}

    /**
     * Throws 403 if {@code rolesHeader} (comma-separated) does not contain {@code required}.
     * {@code rolesHeader} is the value of the X-User-Roles header set by PactaTokenFilter.
     */
    public static void require(String rolesHeader, String required) {
        if (rolesHeader == null) throw forbidden(required);
        for (String role : rolesHeader.split(",")) {
            if (required.equals(role.trim())) return;
        }
        throw forbidden(required);
    }

    private static ResponseStatusException forbidden(String role) {
        return new ResponseStatusException(HttpStatus.FORBIDDEN, "Requires role: " + role);
    }
}
