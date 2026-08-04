package com.factoryops.common.util;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * Small set of shared date/time helpers. Extend as common formatting
 * or conversion needs emerge across services.
 */
public final class DateUtils {

    private DateUtils() {
        // utility class
    }

    public static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss").withZone(ZoneOffset.UTC);

    public static String formatUtc(Instant instant) {
        return DATE_TIME_FORMATTER.format(instant);
    }

    public static Instant nowUtc() {
        return Instant.now();
    }
}
