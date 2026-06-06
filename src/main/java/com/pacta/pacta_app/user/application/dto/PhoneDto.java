package com.pacta.pacta_app.user.application.dto;

import com.pacta.pacta_app.user.domain.Phone;

public record PhoneDto(String indicative, String number) {

    public static PhoneDto from(Phone p) {
        return p == null ? null : new PhoneDto(p.indicative(), p.number());
    }

    public Phone toDomain() {
        return new Phone(indicative, number);
    }
}
