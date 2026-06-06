package com.pacta.pacta_app.auth.infrastructure.controller;

import com.pacta.pacta_app.auth.application.AuthResponse;
import com.pacta.pacta_app.auth.application.AuthService;
import com.pacta.pacta_app.user.domain.Phone;
import jakarta.validation.Valid;
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
        Phone phone = request.phone() != null
                ? new Phone(request.phone().indicative(), request.phone().number())
                : null;
        authService.register(request.email(), request.fullName(), phone, request.country());
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

}
