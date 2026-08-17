package com.vietsoftware.roommanagement.enums;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Enumeration representing the operational status of a room entity.
 */
@Schema(description = "Room operational status")
public enum RoomStatus {
    /**
     * Room is active, operational, and publicly visible to users.
     */
    ACTIVE,

    /**
     * Room is inactive, under maintenance, or soft-deleted, visible only to administrators.
     */
    INACTIVE
}
