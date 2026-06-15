package com.pacta.pacta_app.property.domain.spec;

import com.pacta.pacta_app.property.domain.PropertyType;

public final class PropertyTypeSpecFactory {

    private static final ApartmentSpec APARTMENT = new ApartmentSpec();
    private static final HouseSpec     HOUSE     = new HouseSpec();
    private static final ParkingSpec   PARKING   = new ParkingSpec();
    private static final RoomSpec      ROOM      = new RoomSpec();

    private PropertyTypeSpecFactory() {}

    public static PropertyTypeSpec forType(PropertyType type) {
        return switch (type) {
            case APARTMENT -> APARTMENT;
            case HOUSE     -> HOUSE;
            case PARKING   -> PARKING;
            case ROOM      -> ROOM;
        };
    }
}
