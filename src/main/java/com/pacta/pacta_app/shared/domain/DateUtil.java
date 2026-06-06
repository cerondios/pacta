package com.pacta.pacta_app.shared.domain;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public final class DateUtil {

    public static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                    .withZone(ZoneOffset.UTC);

    private DateUtil() {}

    public static String now() {
        return FORMATTER.format(Instant.now());
    }

    public static String format(Instant instant) {
        return instant != null ? FORMATTER.format(instant) : null;
    }

    public static Instant toInstant(String date) {
        return date != null ? Instant.from(FORMATTER.parse(date)) : null;
    }
}
