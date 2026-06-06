package com.pacta.pacta_app.kyc.infrastructure.controller;

import com.pacta.pacta_app.kyc.application.KycService;
import com.pacta.pacta_app.kyc.application.dto.KycRequest;
import com.pacta.pacta_app.kyc.application.dto.KycResponse;
import com.pacta.pacta_app.shared.infrastructure.filter.PactaTokenFilter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/kyc")
@RequiredArgsConstructor
public class KycController {

    private final KycService kycService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public KycResponse submit(@RequestHeader(PactaTokenFilter.HEADER_USER_ID) String userId,
                              @Valid @RequestBody KycRequest req) {
        return KycResponse.from(kycService.submit(userId, req.frontKey(), req.rearKey(), req.selfieKey()));
    }

    @GetMapping("/me")
    public KycResponse getMine(@RequestHeader(PactaTokenFilter.HEADER_USER_ID) String userId) {
        return KycResponse.from(kycService.getByUserIdOrThrow(userId));
    }

    @GetMapping("/{userId}")
    public KycResponse getByUser(@PathVariable String userId) {
        return KycResponse.from(kycService.getByUserIdOrThrow(userId));
    }

    @PatchMapping("/{userId}/approve")
    public KycResponse approve(@PathVariable String userId,
                               @RequestHeader(PactaTokenFilter.HEADER_USER_ID) String reviewerId) {
        return KycResponse.from(kycService.approve(userId, reviewerId));
    }

    @PatchMapping("/{userId}/reject")
    public KycResponse reject(@PathVariable String userId,
                              @RequestHeader(PactaTokenFilter.HEADER_USER_ID) String reviewerId) {
        return KycResponse.from(kycService.reject(userId, reviewerId));
    }
}
