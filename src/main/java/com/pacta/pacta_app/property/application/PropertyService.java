package com.pacta.pacta_app.property.application;

import com.pacta.pacta_app.property.application.dto.UpdatePropertyRequest;
import com.pacta.pacta_app.property.domain.IPropertyRepository;
import com.pacta.pacta_app.property.domain.Property;
import com.pacta.pacta_app.property.domain.PropertyStatus;
import com.pacta.pacta_app.shared.domain.DateUtil;
import com.pacta.pacta_app.shared.domain.IdGenerator;
import com.pacta.pacta_app.shared.domain.MetricRecorder;
import com.pacta.pacta_app.user.domain.IUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PropertyService {

    private final IPropertyRepository properties;
    private final IUserRepository     users;
    private final MetricRecorder      metrics;
    private final IdGenerator         ids;

    @Transactional
    public Property create(String landlordId) {
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
    public Property update(String propertyId, String landlordId, UpdatePropertyRequest req) {
        Property p = requireOwned(propertyId, landlordId);
        if (!p.isDraft())
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Only DRAFT properties can be updated");

        var b = p.toBuilder().updatedAt(DateUtil.now());
        if (req.city()              != null) b.city(req.city());
        if (req.bankAccountId()     != null) b.bankAccountId(req.bankAccountId());
        if (req.type()              != null) b.type(req.type());
        if (req.areaM2()            != null) b.areaM2(req.areaM2());
        if (req.bedrooms()          != null) b.bedrooms(req.bedrooms());
        if (req.bathrooms()         != null) b.bathrooms(req.bathrooms());
        if (req.floor()             != null) b.floor(req.floor());
        if (req.stratum()           != null) b.stratum(req.stratum());
        if (req.parkingSpots()      != null) b.parkingSpots(req.parkingSpots());
        if (req.amenities()         != null) b.amenities(req.amenities());
        if (req.photoUrls()         != null) b.photos(req.photoUrls());
        if (req.description()       != null) b.description(req.description());
        if (req.monthlyRentCents()  != null) b.monthlyRentCents(req.monthlyRentCents());
        if (req.adminFeeCents()     != null) b.adminFeeCents(req.adminFeeCents());
        if (req.minContractMonths() != null) b.minContractMonths(req.minContractMonths());
        if (req.allowsPets()        != null) b.allowsPets(req.allowsPets());
        if (req.allowsSmokers()     != null) b.allowsSmokers(req.allowsSmokers());
        if (req.allowsChildren()    != null) b.allowsChildren(req.allowsChildren());

        return properties.save(b.build());
    }

    @Transactional
    public Property submit(String propertyId, String landlordId) {
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
    public Property pause(String propertyId, String landlordId) {
        Property p = requireOwned(propertyId, landlordId);
        try {
            return properties.save(p.pause());
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @Transactional
    public Property resume(String propertyId, String landlordId) {
        Property p = requireOwned(propertyId, landlordId);
        try {
            return properties.save(p.resume());
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @Transactional
    public Property archive(String propertyId, String landlordId) {
        Property p = requireOwned(propertyId, landlordId);
        try {
            return properties.save(p.archive());
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
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

    public List<Property> findByLandlordId(String landlordId) {
        return properties.findByLandlordId(landlordId);
    }

    public Property findById(String propertyId) {
        return requireExists(propertyId);
    }

    public List<Property> findPublished() {
        return properties.findByStatus(PropertyStatus.PUBLISHED);
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
