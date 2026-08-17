package com.vietsoftware.roommanagement.constant;

/**
 * Constant utility class holding security-related HTTP headers and JWT token claims.
 */
public final class SecurityConstants {

    private SecurityConstants() {}

    /**
     * HTTP Authorization header key name.
     */
    public static final String AUTH_HEADER = "Authorization";

    /**
     * Bearer token prefix format string.
     */
    public static final String BEARER_PREFIX = "Bearer ";

    /**
     * JWT claim key for user roles array.
     */
    public static final String CLAIM_ROLES = "roles";

    /**
     * JWT claim key for username string.
     */
    public static final String CLAIM_USERNAME = "username";

}
