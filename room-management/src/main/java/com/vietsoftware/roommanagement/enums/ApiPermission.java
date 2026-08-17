package com.vietsoftware.roommanagement.enums;

import lombok.Getter;
import org.springframework.util.AntPathMatcher;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Enumeration defining all API permissions, mapping URI patterns and HTTP methods to allowed roles.
 *
 * <p>This enum is the <strong>single source of truth</strong> for authorization rules.
 * It is synchronized to the DB {@code permissions} table during application startup via
 * {@link com.vietsoftware.roommanagement.configuration.DataInitializer}.</p>
 *
 * <p>An empty {@code allowedRoles} set indicates a <strong>public</strong> endpoint
 * that can be accessed without authentication.</p>
 */
@Getter
public enum ApiPermission {

    // ─── AUTHENTICATION (PUBLIC) ─────────────────────────────────────────
    /**
     * Public endpoint for user registration.
     */
    AUTH_REGISTER("/api/v1/auth/register", "POST", Set.of()),

    /**
     * Public endpoint for user login (obtain access + refresh token).
     */
    AUTH_LOGIN("/api/v1/auth/login", "POST", Set.of()),

    /**
     * Public endpoint for obtaining a new access token via refresh token.
     */
    AUTH_REFRESH("/api/v1/auth/refresh", "POST", Set.of()),

    // ─── SWAGGER UI (PUBLIC) ─────────────────────────────────────────────
    /**
     * Public endpoints for Swagger UI and OpenAPI documentation.
     */
    SWAGGER_UI("/swagger-ui/**", "GET", Set.of()),
    API_DOCS("/v3/api-docs", "GET", Set.of()),
    API_DOCS_CONFIG("/v3/api-docs/**", "GET", Set.of()),
    SWAGGER_UI_HTML("/swagger-ui.html", "GET", Set.of()),

    // ─── AUTHENTICATED ACTIONS (USER AND ADMIN) ──────────────────────────
    /**
     * Endpoint for logging out (invalidates access token and revokes refresh token).
     * Requires authentication.
     */
    AUTH_LOGOUT("/api/v1/auth/logout", "POST", Set.of(RoleType.USER, RoleType.ADMIN)),

    // ─── ROOM (USER & ADMIN) ─────────────────────────────────────────────
    /**
     * Search and paginate active rooms (accessible by USER and ADMIN).
     */
    ROOM_SEARCH_ACTIVE("/api/v1/rooms/active", "GET", Set.of(RoleType.USER, RoleType.ADMIN)),

    /**
     * Retrieve active room details by ID (accessible by USER and ADMIN).
     */
    ROOM_GET_ACTIVE_BY_ID("/api/v1/rooms/active/*", "GET", Set.of(RoleType.USER, RoleType.ADMIN)),

    // ─── ROOM (ADMIN ONLY) ────────────────────────────────────────────────
    /**
     * Search and paginate all rooms across all statuses (ADMIN only).
     */
    ROOM_SEARCH_ALL("/api/v1/rooms", "GET", Set.of(RoleType.ADMIN)),

    /**
     * Retrieve room details by ID regardless of status (ADMIN only).
     */
    ROOM_GET_BY_ID("/api/v1/rooms/*", "GET", Set.of(RoleType.ADMIN)),

    /**
     * Create a new room entry (ADMIN only).
     */
    ROOM_CREATE("/api/v1/rooms", "POST", Set.of(RoleType.ADMIN)),

    /**
     * Update room details (ADMIN only).
     */
    ROOM_UPDATE("/api/v1/rooms/*", "PUT", Set.of(RoleType.ADMIN)),

    /**
     * Update room operational status (ADMIN only).
     */
    ROOM_UPDATE_STATUS("/api/v1/rooms/*/status", "PATCH", Set.of(RoleType.ADMIN)),

    /**
     * Soft delete a room entry (ADMIN only).
     */
    ROOM_DELETE("/api/v1/rooms/*", "DELETE", Set.of(RoleType.ADMIN));

    /**
     * Ant-style URI path pattern for matching incoming request URIs.
     */
    private final String uriPattern;

    /**
     * HTTP method name (e.g. "GET", "POST", "PUT", "PATCH", "DELETE").
     */
    private final String httpMethod;

    /**
     * Set of role types allowed to access this endpoint.
     * An empty set means the endpoint is public (no authentication required).
     */
    private final Set<RoleType> allowedRoles;

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    /**
     * Constructor for each enum entry.
     *
     * @param uriPattern   Ant-style path pattern
     * @param httpMethod   HTTP method name
     * @param allowedRoles set of permitted role types (empty for public)
     */
    ApiPermission(String uriPattern, String httpMethod, Set<RoleType> allowedRoles) {
        this.uriPattern = uriPattern;
        this.httpMethod = httpMethod;
        this.allowedRoles = allowedRoles;
    }

    /**
     * Checks if this permission represents a public endpoint (no authentication required).
     *
     * @return {@code true} if {@code allowedRoles} is empty, {@code false} otherwise
     */
    public boolean isPublic() {
        return allowedRoles.isEmpty();
    }

    /**
     * Returns set of allowed role names as String identifiers.
     *
     * @return set of role name strings
     */
    public Set<String> getAllowedRoleNames() {
        return allowedRoles.stream()
                .map(RoleType::name)
                .collect(Collectors.toSet());
    }

    /**
     * Finds the matching {@link ApiPermission} entry for a given request URI and HTTP method using Ant path matching.
     *
     * @param requestUri incoming request URI path
     * @param httpMethod HTTP request method
     * @return {@link Optional} containing the matched permission, or empty if no match found
     */
    public static Optional<ApiPermission> findMatch(String requestUri, String httpMethod) {
        return Arrays.stream(values())
                .filter(permission -> permission.httpMethod.equalsIgnoreCase(httpMethod)
                        && PATH_MATCHER.match(permission.uriPattern, requestUri))
                .findFirst();
    }

    /**
     * Evaluates whether any of the user's assigned roles are allowed to access this permission.
     *
     * @param userRoles set of user role names from the JWT token
     * @return {@code true} if access is permitted, {@code false} otherwise
     */
    public boolean isAccessibleBy(Set<String> userRoles) {
        if (isPublic()) {
            return true;
        }
        Set<String> allowedNames = getAllowedRoleNames();
        return userRoles.stream().anyMatch(allowedNames::contains);
    }
}
