package com.pacta.pacta_app.compliance.infrastructure.controller;

import com.pacta.pacta_app.compliance.application.ComplianceDocumentService;
import com.pacta.pacta_app.compliance.application.DocumentConfigService;
import com.pacta.pacta_app.compliance.application.dto.*;
import com.pacta.pacta_app.compliance.domain.DocumentType;
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
    private final DocumentConfigService     configService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ComplianceDocResponse submit(@RequestHeader(PactaTokenFilter.HEADER_USER_ID) String userId,
                                        @Valid @RequestBody ComplianceDocRequest req) {
        return ComplianceDocResponse.from(
                complianceService.submit(userId, req.type(), req.key(), req.issuedAt()));
    }

    @GetMapping("/me")
    public List<ComplianceDocResponse> getMine(@RequestHeader(PactaTokenFilter.HEADER_USER_ID) String userId) {
        return complianceService.findByUserId(userId).stream().map(ComplianceDocResponse::from).toList();
    }

    @GetMapping("/users/{userId}")
    public List<ComplianceDocResponse> getByUser(@PathVariable String userId) {
        return complianceService.findByUserId(userId).stream().map(ComplianceDocResponse::from).toList();
    }

    @PatchMapping("/{docId}/approve")
    public ComplianceDocResponse approve(@PathVariable String docId,
                                         @RequestHeader(PactaTokenFilter.HEADER_USER_ID) String reviewerId) {
        return ComplianceDocResponse.from(complianceService.approve(docId, reviewerId));
    }

    @PatchMapping("/{docId}/reject")
    public ComplianceDocResponse reject(@PathVariable String docId,
                                        @RequestHeader(PactaTokenFilter.HEADER_USER_ID) String reviewerId) {
        return ComplianceDocResponse.from(complianceService.reject(docId, reviewerId));
    }

    // ── Document configs ──────────────────────────────────────────────────────

    @GetMapping("/configs")
    public List<DocumentConfigResponse> getConfigs() {
        return configService.findAll().stream().map(DocumentConfigResponse::from).toList();
    }

    @PutMapping("/configs/{type}")
    public DocumentConfigResponse updateConfig(@PathVariable DocumentType type,
                                               @Valid @RequestBody DocumentConfigRequest req) {
        return DocumentConfigResponse.from(configService.update(type, req.expiryDays(), req.warningDays()));
    }
}
