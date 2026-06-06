package com.pacta.pacta_app.auth.infrastructure.controller;

import com.pacta.pacta_app.auth.application.AuthResponse;
import com.pacta.pacta_app.auth.application.AuthService;
import com.pacta.pacta_app.user.domain.Role;

import java.util.Set;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /** Creates an account and emails a one-time login code. */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request.email(), request.roles());
    }

    /** Emails a one-time login code for an existing account. */
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void login(@Valid @RequestBody LoginRequest request) {
        authService.login(request.email());
    }

    /** Exchanges a valid code for a Bearer JWT. */
    @PostMapping("/verify")
    public AuthResponse verify(@Valid @RequestBody VerifyRequest request) {
        return authService.verify(request.email(), request.code());
    }

    public record RegisterRequest(
            @NotBlank @Email String email,
            @NotEmpty Set<Role> roles
    ) {}

    public record LoginRequest(
            @NotBlank @Email String email
    ) {}

    public record VerifyRequest(
            @NotBlank @Email String email,
            @NotBlank @Pattern(regexp = "\\d{6}", message = "code must be 6 digits") String code
    ) {}
}
