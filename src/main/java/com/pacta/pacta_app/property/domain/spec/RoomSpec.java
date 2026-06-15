package com.pacta.pacta_app.property.domain.spec;

import com.pacta.pacta_app.property.domain.Property;

import java.util.ArrayList;
import java.util.List;

public class RoomSpec implements PropertyTypeSpec {

    @Override
    public boolean isReadyToPublish(Property p) {
        return missingFields(p).isEmpty();
    }

    @Override
    public List<String> missingFields(Property p) {
        List<String> missing = new ArrayList<>();
        if (blank(p.getTitle()))                               missing.add("title");
        if (blank(p.getCity()))                                missing.add("city");
        if (blank(p.getNeighborhood()))                        missing.add("neighborhood");
        if (blank(p.getAddress()))                             missing.add("address");
        if (p.getArea() == null || p.getArea() <= 0)          missing.add("area");
        if (p.getMonthlyRent() == null)                        missing.add("monthlyRent");
        if (blank(p.getCurrency()))                            missing.add("currency");
        if (p.getPhotos() == null || p.getPhotos().size() < 2) missing.add("photos (min 2)");
        return missing;
    }

    private boolean blank(String s) { return s == null || s.isBlank(); }
}
