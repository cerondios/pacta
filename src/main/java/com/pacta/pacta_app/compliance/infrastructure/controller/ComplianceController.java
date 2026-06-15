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
@RequiredArgsConstructor
public class ComplianceController {

    private final ComplianceDocumentService complianceService;
    private final ComplianceDocConfigService configService;

    // ── User: manage own documents (/api/me/documents lives in ProfileController) ──

    @DeleteMapping("/api/me/documents/{docId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDoc(
            @PathVariable String docId,
            @RequestHeader(PactaTokenFilter.HEADER_USER_ID) String userId) {
        complianceService.delete(docId, userId);
    }

    // ── Admin: compliance document configs ────────────────────────────────────

    @GetMapping("/api/admin/compliance/configs")
    public List<ComplianceDocConfigResponse> getConfigs(
            @RequestHeader(OperatorTokenFilter.HEADER_OPERATOR_ID) String operatorId,
            @RequestParam(required = false) String country) {
        var configs = country != null ? configService.findByCountry(operatorId, country) : configService.findAll(operatorId);
        return configs.stream().map(ComplianceDocConfigResponse::from).toList();
    }

    @PostMapping("/api/admin/compliance/configs")
    @ResponseStatus(HttpStatus.CREATED)
    public ComplianceDocConfigResponse createConfig(
            @RequestHeader(OperatorTokenFilter.HEADER_OPERATOR_ID) String operatorId,
            @Valid @RequestBody ComplianceDocConfigRequest req) {
        return ComplianceDocConfigResponse.from(
                configService.create(operatorId, req.countryCode(), req.typeCode(), req.displayName()));
    }

    @DeleteMapping("/api/admin/compliance/configs/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteConfig(
            @PathVariable String id,
            @RequestHeader(OperatorTokenFilter.HEADER_OPERATOR_ID) String operatorId) {
        configService.delete(operatorId, id);
    }
}
