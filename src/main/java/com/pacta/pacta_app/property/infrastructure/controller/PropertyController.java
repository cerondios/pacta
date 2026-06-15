package com.pacta.pacta_app.property.infrastructure.controller;

import com.pacta.pacta_app.property.application.PropertyService;
import com.pacta.pacta_app.property.application.dto.PropertyRequestResponse;
import com.pacta.pacta_app.property.application.dto.PropertyResponse;
import com.pacta.pacta_app.property.application.dto.PropertySearchRequest;
import com.pacta.pacta_app.property.domain.Property;
import com.pacta.pacta_app.shared.domain.PagedResult;
import com.pacta.pacta_app.shared.domain.StorageService;
import com.pacta.pacta_app.property.application.dto.UpdatePropertyRequest;
import com.pacta.pacta_app.shared.infrastructure.filter.PactaTokenFilter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
public class PropertyController {

    private final PropertyService propertyService;
    private final StorageService  storageService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PropertyResponse create(
            @RequestHeader(PactaTokenFilter.HEADER_USER_ID) String landlordId,
            @RequestHeader(PactaTokenFilter.HEADER_USER_ROLES) String roles) {
        return PropertyResponse.from(propertyService.create(landlordId, roles), storageService);
    }

    @PatchMapping("/{id}")
    public PropertyResponse update(
            @PathVariable String id,
            @RequestHeader(PactaTokenFilter.HEADER_USER_ID) String landlordId,
            @RequestHeader(PactaTokenFilter.HEADER_USER_ROLES) String roles,
            @Valid @RequestBody UpdatePropertyRequest req) {
        return PropertyResponse.from(propertyService.update(id, landlordId, roles, req), storageService);
    }

    @PostMapping("/{id}/submit")
    public PropertyResponse submit(
            @PathVariable String id,
            @RequestHeader(PactaTokenFilter.HEADER_USER_ID) String landlordId,
            @RequestHeader(PactaTokenFilter.HEADER_USER_ROLES) String roles) {
        return PropertyResponse.from(propertyService.submit(id, landlordId, roles), storageService);
    }

    @PostMapping("/{id}/pause")
    public PropertyResponse pause(
            @PathVariable String id,
            @RequestHeader(PactaTokenFilter.HEADER_USER_ID) String landlordId,
            @RequestHeader(PactaTokenFilter.HEADER_USER_ROLES) String roles) {
        return PropertyResponse.from(propertyService.pause(id, landlordId, roles), storageService);
    }

    @PostMapping("/{id}/resume")
    public PropertyResponse resume(
            @PathVariable String id,
            @RequestHeader(PactaTokenFilter.HEADER_USER_ID) String landlordId,
            @RequestHeader(PactaTokenFilter.HEADER_USER_ROLES) String roles) {
        return PropertyResponse.from(propertyService.resume(id, landlordId, roles), storageService);
    }

    @PostMapping("/{id}/restore")
    public PropertyResponse restore(
            @PathVariable String id,
            @RequestHeader(PactaTokenFilter.HEADER_USER_ID) String landlordId,
            @RequestHeader(PactaTokenFilter.HEADER_USER_ROLES) String roles) {
        return PropertyResponse.from(propertyService.restore(id, landlordId, roles), storageService);
    }

    @PostMapping("/{id}/archive")
    public PropertyResponse archive(
            @PathVariable String id,
            @RequestHeader(PactaTokenFilter.HEADER_USER_ID) String landlordId,
            @RequestHeader(PactaTokenFilter.HEADER_USER_ROLES) String roles) {
        return PropertyResponse.from(propertyService.archive(id, landlordId, roles), storageService);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable String id,
            @RequestHeader(PactaTokenFilter.HEADER_USER_ID) String landlordId,
            @RequestHeader(PactaTokenFilter.HEADER_USER_ROLES) String roles) {
        propertyService.delete(id, landlordId, roles);
    }

    @GetMapping("/search")
    public PagedResult<PropertyResponse> search(
            @RequestHeader(PactaTokenFilter.HEADER_USER_ID) String userId,
            @RequestParam(required = false)                                   String  city,
            @RequestParam(required = false)                                   String  neighborhood,
            @RequestParam(required = false)                                   String  country,
            @RequestParam(required = false)                                   String  type,
            @RequestParam(required = false)                                   String  currency,
            @RequestParam(name = "min_rent",            required = false)     Long    minRent,
            @RequestParam(name = "max_rent",            required = false)     Long    maxRent,
            @RequestParam(name = "min_area",            required = false)     Integer minArea,
            @RequestParam(name = "max_area",            required = false)     Integer maxArea,
            @RequestParam(name = "min_bedrooms",        required = false)     Integer minBedrooms,
            @RequestParam(name = "min_bathrooms",       required = false)     Integer minBathrooms,
            @RequestParam(name = "min_floors",          required = false)     Integer minFloors,
            @RequestParam(name = "min_parking_spots",   required = false)     Integer minParkingSpots,
            @RequestParam(name = "allows_pets",         required = false)     Boolean allowsPets,
            @RequestParam(name = "allows_smokers",      required = false)     Boolean allowsSmokers,
            @RequestParam(name = "allows_children",     required = false)     Boolean allowsChildren,
            @RequestParam(name = "min_contract_months", required = false)     Integer minContractMonths,
            @RequestParam(name = "max_contract_months", required = false)     Integer maxContractMonths,
            @RequestParam(required = false)                                   String  amenity,
            @RequestParam(name = "sort_by",             required = false)     String  sortBy,
            @RequestParam(name = "sort_type",           required = false)     String  sortType,
            @RequestParam(defaultValue = "0")                                 int     page,
            @RequestParam(defaultValue = "12")                                int     size) {
        var req = new PropertySearchRequest(city, neighborhood, country, type, currency,
                minRent, maxRent, minArea, maxArea, minBedrooms, minBathrooms, minFloors,
                minParkingSpots, allowsPets, allowsSmokers, allowsChildren,
                minContractMonths, maxContractMonths, amenity, sortBy, sortType, userId, page, size);
        PagedResult<Property> result = propertyService.search(req);
        return new PagedResult<>(
                result.content().stream().map(p -> PropertyResponse.from(p, storageService)).toList(),
                result.page(), result.size(), result.totalElements(), result.totalPages()
        );
    }

    @PostMapping("/{id}/apply")
    @ResponseStatus(HttpStatus.CREATED)
    public PropertyRequestResponse apply(
            @PathVariable String id,
            @RequestHeader(PactaTokenFilter.HEADER_USER_ID) String userId) {
        return PropertyRequestResponse.from(propertyService.apply(id, userId));
    }

    @GetMapping("/my-requests")
    public List<PropertyRequestResponse> myRequests(
            @RequestHeader(PactaTokenFilter.HEADER_USER_ID) String tenantId) {
        return propertyService.findMyRequests(tenantId).stream()
                .map(PropertyRequestResponse::from).toList();
    }

    @PostMapping("/requests/{requestId}/accept")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void acceptRequest(
            @PathVariable String requestId,
            @RequestHeader(PactaTokenFilter.HEADER_USER_ID) String landlordId) {
        propertyService.acceptRequest(requestId, landlordId);
    }

    @PostMapping("/requests/{requestId}/reject")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void rejectRequest(
            @PathVariable String requestId,
            @RequestHeader(PactaTokenFilter.HEADER_USER_ID) String landlordId) {
        propertyService.rejectRequest(requestId, landlordId);
    }

    @GetMapping("/{id}/requests")
    public List<PropertyRequestResponse> getRequests(
            @PathVariable String id,
            @RequestHeader(PactaTokenFilter.HEADER_USER_ID) String landlordId) {
        return propertyService.findRequestsByProperty(id, landlordId).stream()
                .map(PropertyRequestResponse::from).toList();
    }

    @GetMapping("/{id}")
    public PropertyResponse getById(@PathVariable String id) {
        return PropertyResponse.from(propertyService.findById(id), storageService);
    }

    @GetMapping
    public List<PropertyResponse> get(
            @RequestHeader(PactaTokenFilter.HEADER_USER_ID) String landlordId,
            @RequestHeader(PactaTokenFilter.HEADER_USER_ROLES) String roles) {
        return propertyService.findOwnProperties(landlordId, roles).stream()
                .map(p -> PropertyResponse.from(p, storageService)).toList();
    }
}
