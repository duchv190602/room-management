package com.vietsoftware.roommanagement.constant;

/**
 * Constant utility class holding security-related token claims, headers, roles, and group names.
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

    /**
     * Role identifier for regular users.
     */
    public static final String ROLE_USER = "USER";

    /**
     * Role identifier for administrator users.
     */
    public static final String ROLE_ADMIN = "ADMIN";

    /**
     * Default group name for regular users.
     */
    public static final String GROUP_DEFAULT_USER = "DEFAULT_USER_GROUP";

    /**
     * Group name for administrator users.
     */
    public static final String GROUP_ADMIN = "ADMIN_GROUP";

}
