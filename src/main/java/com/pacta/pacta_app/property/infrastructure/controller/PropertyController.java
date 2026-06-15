package com.pacta.pacta_app.property.infrastructure.controller;

import com.pacta.pacta_app.property.application.PropertyService;
import com.pacta.pacta_app.property.application.dto.PropertyResponse;
import com.pacta.pacta_app.property.application.dto.UpdatePropertyRequest;
import com.pacta.pacta_app.shared.infrastructure.filter.PactaTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
public class PropertyController {

    private final PropertyService propertyService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PropertyResponse create(
            @RequestHeader(PactaTokenFilter.HEADER_USER_ID) String landlordId) {
        return PropertyResponse.from(propertyService.create(landlordId));
    }

    @PatchMapping("/{id}")
    public PropertyResponse update(
            @PathVariable String id,
            @RequestHeader(PactaTokenFilter.HEADER_USER_ID) String landlordId,
            @RequestBody UpdatePropertyRequest req) {
        return PropertyResponse.from(propertyService.update(id, landlordId, req));
    }

    @PostMapping("/{id}/submit")
    public PropertyResponse submit(
            @PathVariable String id,
            @RequestHeader(PactaTokenFilter.HEADER_USER_ID) String landlordId) {
        return PropertyResponse.from(propertyService.submit(id, landlordId));
    }

    @PostMapping("/{id}/pause")
    public PropertyResponse pause(
            @PathVariable String id,
            @RequestHeader(PactaTokenFilter.HEADER_USER_ID) String landlordId) {
        return PropertyResponse.from(propertyService.pause(id, landlordId));
    }

    @PostMapping("/{id}/resume")
    public PropertyResponse resume(
            @PathVariable String id,
            @RequestHeader(PactaTokenFilter.HEADER_USER_ID) String landlordId) {
        return PropertyResponse.from(propertyService.resume(id, landlordId));
    }

    @PostMapping("/{id}/archive")
    public PropertyResponse archive(
            @PathVariable String id,
            @RequestHeader(PactaTokenFilter.HEADER_USER_ID) String landlordId) {
        return PropertyResponse.from(propertyService.archive(id, landlordId));
    }

    @GetMapping("/me")
    public List<PropertyResponse> getMyProperties(
            @RequestHeader(PactaTokenFilter.HEADER_USER_ID) String landlordId) {
        return propertyService.findByLandlordId(landlordId).stream()
                .map(PropertyResponse::from).toList();
    }

    @GetMapping("/{id}")
    public PropertyResponse getById(@PathVariable String id) {
        return PropertyResponse.from(propertyService.findById(id));
    }

    @GetMapping
    public List<PropertyResponse> getPublished() {
        return propertyService.findPublished().stream()
                .map(PropertyResponse::from).toList();
    }
}
