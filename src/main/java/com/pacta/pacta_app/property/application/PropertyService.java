package com.pacta.pacta_app.property.application;

import com.pacta.pacta_app.property.application.dto.PropertySearchRequest;
import com.pacta.pacta_app.property.application.dto.UpdatePropertyRequest;
import com.pacta.pacta_app.property.domain.IPropertyRepository;
import com.pacta.pacta_app.property.domain.IPropertyRequestRepository;
import com.pacta.pacta_app.property.domain.Property;
import com.pacta.pacta_app.property.domain.PropertyRequest;
import com.pacta.pacta_app.property.domain.PropertyRequestStatus;
import com.pacta.pacta_app.property.domain.PropertyStatus;
import com.pacta.pacta_app.shared.domain.DateUtil;
import com.pacta.pacta_app.shared.domain.IdGenerator;
import com.pacta.pacta_app.shared.domain.MetricRecorder;
import com.pacta.pacta_app.shared.domain.RoleGuard;
import com.pacta.pacta_app.shared.domain.StorageService;
import com.pacta.pacta_app.user.domain.IUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.pacta.pacta_app.shared.domain.PagedResult;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PropertyService {

    private final IPropertyRepository        properties;
    private final IPropertyRequestRepository requests;
    private final IUserRepository            users;
    private final MetricRecorder             metrics;
    private final IdGenerator                ids;
    private final StorageService             storage;

    @Transactional
    public Property create(String landlordId, String roles) {
        RoleGuard.require(roles, "LANDLORD");
        var landlord = users.findById(landlordId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + landlordId));
        if (!landlord.isCompleted())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Only COMPLETED users can create properties, current status: " + landlord.getStatus());
        Property draft = Property.create(ids, landlordId);
        properties.save(draft);
        metrics.incrementCounter("property.created");
        return draft;
    }

    @Transactional
    public Property update(String propertyId, String landlordId, String roles, UpdatePropertyRequest req) {
        RoleGuard.require(roles, "LANDLORD");
        Property p = requireOwned(propertyId, landlordId);
        if (p.getStatus() == PropertyStatus.BLOCKED || p.getStatus() == PropertyStatus.ARCHIVED)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "BLOCKED and ARCHIVED properties cannot be updated");

        var b = p.toBuilder().updatedAt(DateUtil.now());
        if (req.purpose()           != null) b.purpose(req.purpose());
        if (req.title()             != null) b.title(req.title());
        if (req.country()           != null) b.country(req.country());
        if (req.city()              != null) b.city(req.city());
        if (req.neighborhood()      != null) b.neighborhood(req.neighborhood());
        if (req.address()           != null) b.address(req.address());
        if (req.type()              != null) b.type(req.type());
        if (req.area()              != null) b.area(req.area());
        if (req.areaUnit()          != null) b.areaUnit(req.areaUnit());
        b.bedrooms(req.bedrooms());
        b.bathrooms(req.bathrooms());
        b.floors(req.floors());
        if (req.parkingSpots()      != null) b.parkingSpots(req.parkingSpots());
        if (req.amenities()         != null) b.amenities(req.amenities());
        if (req.photoKeys()         != null) b.photos(req.photoKeys());
        if (req.description()       != null) b.description(req.description());
        if (req.monthlyRent()       != null) b.monthlyRent(req.monthlyRent());
        if (req.currency()          != null) b.currency(req.currency());
        if (req.adminFee()          != null) b.adminFee(req.adminFee());
        if (req.minContractMonths() != null) b.minContractMonths(req.minContractMonths());
        if (req.allowsPets()        != null) b.allowsPets(req.allowsPets());
        if (req.allowsSmokers()     != null) b.allowsSmokers(req.allowsSmokers());
        if (req.allowsChildren()    != null) b.allowsChildren(req.allowsChildren());

        if (!p.isDraft())
            b.status(PropertyStatus.PENDING_REVIEW);

        return properties.save(b.build());
    }

    @Transactional
    public Property submit(String propertyId, String landlordId, String roles) {
        RoleGuard.require(roles, "LANDLORD");
        Property p = requireOwned(propertyId, landlordId);
        try {
            Property submitted = properties.save(p.submit());
            metrics.incrementCounter("property.submitted");
            return submitted;
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @Transactional
    public Property approve(String propertyId, String reviewerId) {
        Property p = requireExists(propertyId);
        try {
            Property approved = properties.save(p.approve(reviewerId));
            metrics.incrementCounter("property.approved");
            return approved;
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @Transactional
    public Property reject(String propertyId, String reviewerId) {
        Property p = requireExists(propertyId);
        try {
            Property rejected = properties.save(p.reject(reviewerId));
            metrics.incrementCounter("property.rejected");
            return rejected;
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @Transactional
    public Property pause(String propertyId, String landlordId, String roles) {
        RoleGuard.require(roles, "LANDLORD");
        Property p = requireOwned(propertyId, landlordId);
        try {
            return properties.save(p.pause());
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    /** No role guard — called internally right after a landlord accepts a request. */
    @Transactional
    public Property reserve(String propertyId) {
        Property p = requireExists(propertyId);
        try {
            return properties.save(p.reserve());
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    /** No role guard — called internally once both parties sign the deal's contract. */
    @Transactional
    public Property markRented(String propertyId) {
        Property p = requireExists(propertyId);
        try {
            return properties.save(p.rent());
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @Transactional
    public Property resume(String propertyId, String landlordId, String roles) {
        RoleGuard.require(roles, "LANDLORD");
        Property p = requireOwned(propertyId, landlordId);
        try {
            return properties.save(p.resume());
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @Transactional
    public Property archive(String propertyId, String landlordId, String roles) {
        RoleGuard.require(roles, "LANDLORD");
        Property p = requireOwned(propertyId, landlordId);
        try {
            return properties.save(p.archive());
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @Transactional
    public Property restore(String propertyId, String landlordId, String roles) {
        RoleGuard.require(roles, "LANDLORD");
        Property p = requireOwned(propertyId, landlordId);
        try {
            return properties.save(p.restore());
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @Transactional
    public void delete(String propertyId, String landlordId, String roles) {
        RoleGuard.require(roles, "LANDLORD");
        Property p = requireOwned(propertyId, landlordId);
        deletePhotos(p);
        properties.deleteById(propertyId);
    }

    private void deletePhotos(Property p) {
        if (p.getPhotos() == null) return;
        for (String key : p.getPhotos()) {
            try {
                storage.delete(key);
            } catch (Exception e) {
                log.warn("Could not delete photo from storage, key={}", key, e);
            }
        }
    }

    @Transactional
    public Property block(String propertyId, String adminId) {
        Property p = requireExists(propertyId);
        try {
            return properties.save(p.block(adminId));
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @Transactional
    public Property unblock(String propertyId, String adminId) {
        Property p = requireExists(propertyId);
        try {
            return properties.save(p.unblock(adminId));
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    /** Self-service: caller must have LANDLORD role. */
    public List<Property> findOwnProperties(String landlordId, String roles) {
        RoleGuard.require(roles, "LANDLORD");
        return properties.findByLandlordId(landlordId);
    }

    /** Admin/internal: no role guard — caller is already on an operator route. */
    public List<Property> findByLandlordId(String landlordId) {
        return properties.findByLandlordId(landlordId);
    }

    public Property findById(String propertyId) {
        return requireExists(propertyId);
    }

    public List<Property> findPublished() {
        return properties.findByStatus(PropertyStatus.PUBLISHED);
    }

    public PagedResult<Property> search(PropertySearchRequest req) {
        return properties.search(req);
    }

    public List<Property> findPendingReview() {
        return properties.findByStatus(PropertyStatus.PENDING_REVIEW);
    }

    public List<Property> findAll() {
        return properties.findAll();
    }

    @Transactional
    public PropertyRequest apply(String propertyId, String callerId) {
        Property p = requireExists(propertyId);
        if (!PropertyStatus.PUBLISHED.equals(p.getStatus()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Property is not published");
        if (p.getLandlordId().equals(callerId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Owner cannot apply to their own property");
        if (requests.findByTenantIdAndPropertyId(callerId, propertyId).isPresent())
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Already applied to this property");
        PropertyRequest request = PropertyRequest.create(ids, callerId, propertyId);
        metrics.incrementCounter("property.request.created");
        return requests.save(request);
    }

    public List<PropertyRequest> findMyRequests(String tenantId) {
        return requests.findByTenantId(tenantId);
    }

    public List<PropertyRequest> findRequestsByProperty(String propertyId, String landlordId) {
        requireOwned(propertyId, landlordId);
        return requests.findByPropertyId(propertyId);
    }

    /** Every ACCEPTED request across all of this landlord's properties — used to backfill deals. */
    public List<PropertyRequest> findAcceptedRequestsForLandlord(String landlordId) {
        return properties.findByLandlordId(landlordId).stream()
                .flatMap(p -> requests.findByPropertyId(p.getId()).stream())
                .filter(r -> r.getStatus() == PropertyRequestStatus.ACCEPTED)
                .toList();
    }

    public PropertyRequest findRequestById(String requestId) {
        return requireRequest(requestId);
    }

    @Transactional
    public PropertyRequest acceptRequest(String requestId, String landlordId) {
        PropertyRequest req = requireRequest(requestId);
        requireOwned(req.getPropertyId(), landlordId);
        PropertyRequest accepted = requests.save(req.accept(landlordId));
        metrics.incrementCounter("property.request.accepted");
        return accepted;
    }

    @Transactional
    public void rejectRequest(String requestId, String landlordId) {
        PropertyRequest req = requireRequest(requestId);
        requireOwned(req.getPropertyId(), landlordId);
        requests.save(req.reject(landlordId));
        metrics.incrementCounter("property.request.rejected");
    }

    private PropertyRequest requireRequest(String requestId) {
        return requests.findById(requestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Request not found: " + requestId));
    }

    // ── Private ───────────────────────────────────────────────────────────────

    private Property requireExists(String propertyId) {
        return properties.findById(propertyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Property not found: " + propertyId));
    }

    private Property requireOwned(String propertyId, String landlordId) {
        Property p = requireExists(propertyId);
        if (!p.getLandlordId().equals(landlordId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        return p;
    }
}
