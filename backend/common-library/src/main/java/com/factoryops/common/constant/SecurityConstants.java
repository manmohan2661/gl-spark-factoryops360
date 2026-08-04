package com.factoryops.common.constant;

/**
 * Shared constant keys used by security-related concerns
 * (JWT claims, role prefixes, etc.) across services.
 */
public final class SecurityConstants {

    private SecurityConstants() {
        // utility class
    }

    public static final String ROLE_PREFIX = "ROLE_";
    public static final String CLAIM_USER_ID = "userId";
    public static final String CLAIM_ROLES = "roles";
}
