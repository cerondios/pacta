package com.pacta.pacta_app.kyc.application.dto;

import jakarta.validation.constraints.NotBlank;

public record KycRequest(
        @NotBlank String frontKey,
        @NotBlank String rearKey,
        @NotBlank String selfieKey
) {}
