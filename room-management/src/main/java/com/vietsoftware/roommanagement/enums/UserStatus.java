package com.vietsoftware.roommanagement.enums;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Enumeration representing the account lifecycle status of a user.
 */
@Schema(description = "User account status")
public enum UserStatus {
    /**
     * User account is active and allowed to authenticate.
     */
    ACTIVE,

    /**
     * User account is deactivated and blocked from authentication.
     */
    INACTIVE
}
