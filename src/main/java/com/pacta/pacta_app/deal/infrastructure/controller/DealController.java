package com.pacta.pacta_app.deal.infrastructure.controller;

import com.pacta.pacta_app.deal.application.DealService;
import com.pacta.pacta_app.deal.application.dto.DealResponse;
import com.pacta.pacta_app.deal.application.dto.SignDealRequest;
import com.pacta.pacta_app.shared.infrastructure.filter.PactaTokenFilter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/deals")
@RequiredArgsConstructor
public class DealController {

    private final DealService dealService;

    /** "Mis negocios": deals where the caller is the landlord or the tenant. */
    @GetMapping("/my-deals")
    public List<DealResponse> myDeals(@RequestHeader(PactaTokenFilter.HEADER_USER_ID) String userId) {
        return dealService.findMyDeals(userId).stream().map(DealResponse::from).toList();
    }

    @GetMapping("/{id}")
    public DealResponse getById(
            @PathVariable String id,
            @RequestHeader(PactaTokenFilter.HEADER_USER_ID) String userId) {
        return DealResponse.from(dealService.findByIdForUser(id, userId));
    }

    @GetMapping("/{id}/contract-url")
    public Map<String, String> getContractUrl(
            @PathVariable String id,
            @RequestHeader(PactaTokenFilter.HEADER_USER_ID) String userId) {
        return Map.of("url", dealService.getContractViewUrl(id, userId));
    }

    @PostMapping("/{id}/sign")
    public DealResponse sign(
            @PathVariable String id,
            @RequestHeader(PactaTokenFilter.HEADER_USER_ID) String userId,
            @Valid @RequestBody SignDealRequest req) {
        return DealResponse.from(dealService.sign(id, userId, req.signatureName()));
    }
}
