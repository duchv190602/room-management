package com.vietsoftware.roommanagement.constant;

/**
 * Constant utility class holding API paths, pagination default values, and input length constraints.
 */
public final class ApiConstants {

    private ApiConstants() {}

    /**
     * Base path prefix for API v1 endpoints.
     */
    public static final String API_V1 = "/api/v1";

    /**
     * API endpoint path for authentication resources.
     */
    public static final String AUTH_PATH = API_V1 + "/auth";

    /**
     * API endpoint path for room management resources.
     */
    public static final String ROOM_PATH = API_V1 + "/rooms";

    /**
     * Default page index for paginated requests (0-indexed).
     */
    public static final String DEFAULT_PAGE_NUMBER = "0";

    /**
     * Default page size limit for paginated requests.
     */
    public static final String DEFAULT_PAGE_SIZE = "10";

    /**
     * Default sort property field name.
     */
    public static final String DEFAULT_SORT_BY = "createdAt";

    /**
     * Default sort direction string (ASC or DESC).
     */
    public static final String DEFAULT_SORT_DIRECTION = "DESC";

    /**
     * Minimum character length for user login name.
     */
    public static final int USERNAME_MIN_LENGTH = 3;

    /**
     * Maximum character length for user login name.
     */
    public static final int USERNAME_MAX_LENGTH = 50;

    /**
     * Maximum character length for user email address.
     */
    public static final int EMAIL_MAX_LENGTH = 100;

    /**
     * Minimum character length for user password.
     */
    public static final int PASSWORD_MIN_LENGTH = 8;
}
