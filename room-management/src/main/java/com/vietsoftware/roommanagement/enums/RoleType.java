package com.vietsoftware.roommanagement.enums;

import lombok.Getter;

/**
 * Enumeration defining standard security roles in the system.
 *
 * <p>Serves as the single source of truth for core system role names.</p>
 */
@Getter
public enum RoleType {

    /**
     * Role for administrator users with full access permissions.
     */
    ADMIN("Administrator role"),

    /**
     * Role for standard registered users with limited access permissions.
     */
    USER("Regular user role");

    private final String description;

    RoleType(String description) {
        this.description = description;
    }
}
