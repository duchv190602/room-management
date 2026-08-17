package com.vietsoftware.roommanagement.enums;

import lombok.Getter;

/**
 * Enumeration defining standard user group names and their initial default roles.
 *
 * <p>Serves as the single source of truth for default user groups seeded at application startup.</p>
 */
@Getter
public enum UserGroupType {

    /**
     * Administrator group associated with the ADMIN role.
     */
    ADMIN_GROUP("Administrator group", RoleType.ADMIN),

    /**
     * Default user group for newly registered users, associated with the USER role.
     */
    DEFAULT_USER_GROUP("Default user group", RoleType.USER);

    private final String description;
    private final RoleType defaultRole;

    UserGroupType(String description, RoleType defaultRole) {
        this.description = description;
        this.defaultRole = defaultRole;
    }
}
