package com.pacta.pacta_app.compliance.infrastructure.controller;

import com.pacta.pacta_app.compliance.application.ComplianceDocumentService;
import com.pacta.pacta_app.compliance.application.ComplianceDocConfigService;
import com.pacta.pacta_app.compliance.application.dto.*;
import com.pacta.pacta_app.shared.infrastructure.filter.OperatorTokenFilter;
import com.pacta.pacta_app.shared.infrastructure.filter.PactaTokenFilter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/compliance")
@RequiredArgsConstructor
public class ComplianceController {

    private final ComplianceDocumentService complianceService;
    private final ComplianceDocConfigService     configService;

    // ── Documents ─────────────────────────────────────────────────────────────

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ComplianceDocResponse submit(
            @RequestHeader(PactaTokenFilter.HEADER_USER_ID) String userId,
            @Valid @RequestBody ComplianceDocRequest req) {
        return ComplianceDocResponse.from(
                complianceService.submit(userId, req.typeCode(), req.key(), req.issuedAt()));
    }

    @GetMapping("/me")
    public List<ComplianceDocResponse> getMine(
            @RequestHeader(PactaTokenFilter.HEADER_USER_ID) String userId) {
        return complianceService.findByUserId(userId).stream().map(ComplianceDocResponse::from).toList();
    }

    @GetMapping("/users/{userId}")
    public List<ComplianceDocResponse> getByUser(@PathVariable String userId) {
        return complianceService.findByUserId(userId).stream().map(ComplianceDocResponse::from).toList();
    }

    @DeleteMapping("/{docId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDoc(
            @PathVariable String docId,
            @RequestHeader(PactaTokenFilter.HEADER_USER_ID) String userId) {
        complianceService.delete(docId, userId);
    }

    @PatchMapping("/{docId}/approve")
    public ComplianceDocResponse approve(
            @PathVariable String docId,
            @RequestHeader(PactaTokenFilter.HEADER_USER_ID) String reviewerId) {
        return ComplianceDocResponse.from(complianceService.approve(docId, reviewerId));
    }

    @PatchMapping("/{docId}/reject")
    public ComplianceDocResponse reject(
            @PathVariable String docId,
            @RequestHeader(PactaTokenFilter.HEADER_USER_ID) String reviewerId) {
        return ComplianceDocResponse.from(complianceService.reject(docId, reviewerId));
    }

    // ── Document configs (admin only) ─────────────────────────────────────────

    @GetMapping("/configs")
    public List<ComplianceDocConfigResponse> getConfigs(
            @RequestHeader(OperatorTokenFilter.HEADER_OPERATOR_ID) String operatorId,
            @RequestParam(required = false) String country) {
        var configs = country != null ? configService.findByCountry(operatorId, country) : configService.findAll(operatorId);
        return configs.stream().map(ComplianceDocConfigResponse::from).toList();
    }

    @PostMapping("/configs")
    @ResponseStatus(HttpStatus.CREATED)
    public ComplianceDocConfigResponse createConfig(
            @RequestHeader(OperatorTokenFilter.HEADER_OPERATOR_ID) String operatorId,
            @Valid @RequestBody ComplianceDocConfigRequest req) {
        return ComplianceDocConfigResponse.from(
                configService.create(operatorId, req.countryCode(), req.typeCode(), req.displayName()));
    }

    @DeleteMapping("/configs/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteConfig(
            @PathVariable String id,
            @RequestHeader(OperatorTokenFilter.HEADER_OPERATOR_ID) String operatorId) {
        configService.delete(operatorId, id);
    }
}
