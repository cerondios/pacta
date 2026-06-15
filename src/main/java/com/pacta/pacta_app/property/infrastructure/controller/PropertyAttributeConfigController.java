package com.pacta.pacta_app.property.infrastructure.controller;

import com.pacta.pacta_app.property.application.PropertyAttributeConfigService;
import com.pacta.pacta_app.property.application.dto.PropertyAttributeConfigPatchRequest;
import com.pacta.pacta_app.property.application.dto.PropertyAttributeConfigRequest;
import com.pacta.pacta_app.property.application.dto.PropertyAttributeConfigResponse;
import com.pacta.pacta_app.shared.infrastructure.filter.OperatorTokenFilter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/property-configs")
@RequiredArgsConstructor
public class PropertyAttributeConfigController {

    private final PropertyAttributeConfigService service;

    /** Public — landlords call this to populate the amenities/features form. */
    @GetMapping
    public List<PropertyAttributeConfigResponse> getEnabled(
            @RequestParam(required = false) String propertyType) {
        return service.findEnabled(propertyType).stream()
                .map(PropertyAttributeConfigResponse::from).toList();
    }

    /** Admin — full list including disabled entries. */
    @GetMapping("/all")
    public List<PropertyAttributeConfigResponse> getAll(
            @RequestHeader(OperatorTokenFilter.HEADER_OPERATOR_ID) String operatorId) {
        return service.findAll(operatorId).stream()
                .map(PropertyAttributeConfigResponse::from).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PropertyAttributeConfigResponse create(
            @RequestHeader(OperatorTokenFilter.HEADER_OPERATOR_ID) String operatorId,
            @Valid @RequestBody PropertyAttributeConfigRequest req) {
        return PropertyAttributeConfigResponse.from(service.create(operatorId, req));
    }

    @PatchMapping("/{id}")
    public PropertyAttributeConfigResponse patch(
            @PathVariable String id,
            @RequestHeader(OperatorTokenFilter.HEADER_OPERATOR_ID) String operatorId,
            @RequestBody PropertyAttributeConfigPatchRequest req) {
        return PropertyAttributeConfigResponse.from(service.patch(operatorId, id, req));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable String id,
            @RequestHeader(OperatorTokenFilter.HEADER_OPERATOR_ID) String operatorId) {
        service.delete(operatorId, id);
    }
}
