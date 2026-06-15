package com.pacta.pacta_app.deal.application.dto;

import jakarta.validation.constraints.NotBlank;

public record SignDealRequest(@NotBlank String signatureName) {}
