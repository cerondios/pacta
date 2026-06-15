package com.pacta.pacta_app.admin.infrastructure.controller;

import com.pacta.pacta_app.admin.application.AdminService;
import com.pacta.pacta_app.admin.application.dto.AdminResponse;
import com.pacta.pacta_app.admin.application.dto.CreateAdminRequest;
import com.pacta.pacta_app.admin.domain.AdminRole;
import com.pacta.pacta_app.property.application.PropertyService;
import com.pacta.pacta_app.property.application.dto.PropertyResponse;
import com.pacta.pacta_app.shared.domain.StorageService;
import com.pacta.pacta_app.shared.infrastructure.filter.OperatorTokenFilter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/admins")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService    adminService;
    private final PropertyService propertyService;
    private final StorageService  storageService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AdminResponse create(
            @RequestHeader(OperatorTokenFilter.HEADER_OPERATOR_ID) String operatorId,
            @Valid @RequestBody CreateAdminRequest req) {
        return AdminResponse.from(
                adminService.create(operatorId, req.fullName(), req.email(),
                        req.role(), req.password()));
    }

    @GetMapping
    public List<AdminResponse> listAll() {
        return adminService.findAll().stream().map(AdminResponse::from).toList();
    }

    @GetMapping("/{id}")
    public AdminResponse getById(@PathVariable String id) {
        return AdminResponse.from(adminService.getById(id));
    }

    @GetMapping("/reviewers")
    public List<AdminResponse> listReviewers() {
        return adminService.findAllByRole(AdminRole.REVIEWER).stream().map(AdminResponse::from).toList();
    }

    // ── Property moderation ───────────────────────────────────────────────────

    @GetMapping("/properties")
    public List<PropertyResponse> listProperties(
            @RequestParam(required = false) String landlordId) {
        var properties = landlordId != null
                ? propertyService.findByLandlordId(landlordId)
                : propertyService.findAll();
        return properties.stream().map(p -> PropertyResponse.from(p, storageService)).toList();
    }

    @PatchMapping("/properties/{id}/block")
    public PropertyResponse blockProperty(
            @PathVariable String id,
            @RequestHeader(OperatorTokenFilter.HEADER_OPERATOR_ID) String adminId) {
        return PropertyResponse.from(propertyService.block(id, adminId), storageService);
    }

    @PatchMapping("/properties/{id}/unblock")
    public PropertyResponse unblockProperty(
            @PathVariable String id,
            @RequestHeader(OperatorTokenFilter.HEADER_OPERATOR_ID) String adminId) {
        return PropertyResponse.from(propertyService.unblock(id, adminId), storageService);
    }
}
