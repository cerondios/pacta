package com.pacta.pacta_app.property.infrastructure;

import com.pacta.pacta_app.property.application.dto.PropertySearchRequest;
import com.pacta.pacta_app.property.domain.IPropertyRepository;
import com.pacta.pacta_app.property.domain.Property;
import com.pacta.pacta_app.property.domain.PropertyStatus;
import com.pacta.pacta_app.shared.domain.PagedResult;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Profile({"local", "default"})
public class InMemoryPropertyRepository implements IPropertyRepository {

    private final Map<String, Property> store = new ConcurrentHashMap<>();

    @Override
    public Property save(Property property) {
        store.put(property.getId(), property);
        return property;
    }

    @Override
    public Optional<Property> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Property> findByLandlordIdExcludingStatus(String landlordId, PropertyStatus excluded) {
        return store.values().stream()
                .filter(p -> p.getLandlordId().equals(landlordId) && p.getStatus() != excluded)
                .toList();
    }

    public List<Property> findByLandlordId(String landlordId) {
        return store.values().stream()
                .filter(p -> p.getLandlordId().equals(landlordId))
                .toList();
    }

    @Override
    public List<Property> findAll() {
        return List.copyOf(store.values());
    }

    @Override
    public void deleteById(String id) {
        store.remove(id);
    }

    public List<Property> findByStatus(PropertyStatus status) {
        return store.values().stream()
                .filter(p -> p.getStatus() == status)
                .toList();
    }

    @Override
    public PagedResult<Property> search(PropertySearchRequest req) {
        var stream = store.values().stream().filter(p -> p.getStatus() == PropertyStatus.PUBLISHED);
        if (req.excludeLandlordId() != null) stream = stream.filter(p -> !p.getLandlordId().equals(req.excludeLandlordId()));
        if (req.city()              != null) stream = stream.filter(p -> p.getCity()         != null && p.getCity().toLowerCase().contains(req.city().toLowerCase()));
        if (req.neighborhood()      != null) stream = stream.filter(p -> p.getNeighborhood() != null && p.getNeighborhood().toLowerCase().contains(req.neighborhood().toLowerCase()));
        if (req.country()           != null) stream = stream.filter(p -> p.getCountry()      != null && p.getCountry().equalsIgnoreCase(req.country()));
        if (req.type()              != null) stream = stream.filter(p -> p.getType()         != null && p.getType().name().equalsIgnoreCase(req.type()));
        if (req.currency()          != null) stream = stream.filter(p -> p.getCurrency()     != null && p.getCurrency().equalsIgnoreCase(req.currency()));
        if (req.minRent()           != null) stream = stream.filter(p -> p.getMonthlyRent()  != null && p.getMonthlyRent() >= req.minRent());
        if (req.maxRent()           != null) stream = stream.filter(p -> p.getMonthlyRent()  != null && p.getMonthlyRent() <= req.maxRent());
        if (req.minArea()           != null) stream = stream.filter(p -> p.getArea()         != null && p.getArea() >= req.minArea());
        if (req.maxArea()           != null) stream = stream.filter(p -> p.getArea()         != null && p.getArea() <= req.maxArea());
        if (req.minBedrooms()       != null) stream = stream.filter(p -> p.getBedrooms()     != null && p.getBedrooms() >= req.minBedrooms());
        if (req.minBathrooms()      != null) stream = stream.filter(p -> p.getBathrooms()    != null && p.getBathrooms() >= req.minBathrooms());
        if (req.minFloors()         != null) stream = stream.filter(p -> p.getFloors()       != null && p.getFloors() >= req.minFloors());
        if (req.minParkingSpots()   != null) stream = stream.filter(p -> p.getParkingSpots() != null && p.getParkingSpots() >= req.minParkingSpots());
        if (req.allowsPets()        != null) stream = stream.filter(p -> p.isAllowsPets()    == req.allowsPets());
        if (req.allowsSmokers()     != null) stream = stream.filter(p -> p.isAllowsSmokers() == req.allowsSmokers());
        if (req.allowsChildren()    != null) stream = stream.filter(p -> p.isAllowsChildren()== req.allowsChildren());
        if (req.minContractMonths() != null) stream = stream.filter(p -> p.getMinContractMonths() != null && p.getMinContractMonths() >= req.minContractMonths());
        if (req.maxContractMonths() != null) stream = stream.filter(p -> p.getMinContractMonths() != null && p.getMinContractMonths() <= req.maxContractMonths());
        if (req.amenity()           != null) stream = stream.filter(p -> p.getAmenities()    != null && p.getAmenities().contains(req.amenity()));

        var sorted = new java.util.ArrayList<>(stream.toList());
        String sortBy   = req.sortBy()   != null ? req.sortBy()   : "created_at";
        String sortType = req.sortType() != null ? req.sortType().toUpperCase() : "DESC";
        java.util.Comparator<Property> cmp = switch (sortBy) {
            case "monthly_rent" -> java.util.Comparator.comparingLong(p -> p.getMonthlyRent() != null ? p.getMonthlyRent() : (sortType.equals("ASC") ? Long.MAX_VALUE : 0L));
            case "area"         -> java.util.Comparator.comparingDouble(p -> p.getArea() != null ? p.getArea() : (sortType.equals("ASC") ? Double.MAX_VALUE : 0.0));
            case "bedrooms"     -> java.util.Comparator.comparingInt(p -> p.getBedrooms() != null ? p.getBedrooms() : (sortType.equals("ASC") ? Integer.MAX_VALUE : 0));
            default             -> java.util.Comparator.comparing(Property::getCreatedAt, java.util.Comparator.reverseOrder());
        };
        if ("DESC".equals(sortType) && !sortBy.equals("created_at")) cmp = cmp.reversed();
        sorted.sort(cmp);

        long total      = sorted.size();
        int  fromIndex  = req.page() * req.size();
        int  toIndex    = (int) Math.min(fromIndex + req.size(), total);
        int  totalPages = req.size() > 0 ? (int) Math.ceil((double) total / req.size()) : 0;
        List<Property> content = fromIndex >= total ? List.of() : sorted.subList(fromIndex, toIndex);
        return new PagedResult<>(content, req.page(), req.size(), total, totalPages);
    }
}
