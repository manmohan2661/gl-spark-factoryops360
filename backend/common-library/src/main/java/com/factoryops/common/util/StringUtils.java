package com.factoryops.common.util;

/**
 * Small set of shared string helpers usable by any microservice.
 */
public final class StringUtils {

    private StringUtils() {
        // utility class
    }

    public static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static boolean isNotBlank(String value) {
        return !isBlank(value);
    }
}
