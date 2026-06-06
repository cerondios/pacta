package com.pacta.pacta_app.admin.infrastructure.controller;

import com.pacta.pacta_app.admin.application.AdminService;
import com.pacta.pacta_app.admin.application.dto.AdminResponse;
import com.pacta.pacta_app.admin.application.dto.CreateAdminRequest;
import com.pacta.pacta_app.admin.domain.AdminRole;
import com.pacta.pacta_app.shared.infrastructure.filter.OperatorTokenFilter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admins")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

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
}
