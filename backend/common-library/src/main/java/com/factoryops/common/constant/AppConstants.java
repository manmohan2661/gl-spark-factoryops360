package com.factoryops.common.constant;

/**
 * Application-wide constants shared across FactoryOps360 microservices.
 * Extend per-domain as needed; keep this class free of business-specific values.
 */
public final class AppConstants {

    private AppConstants() {
        // utility class
    }

    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String DEFAULT_TIMEZONE = "UTC";

    public static final int DEFAULT_PAGE_NUMBER = 0;
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;

    public static final String AUTH_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
}
