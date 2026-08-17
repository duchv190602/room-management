package com.vietsoftware.roommanagement.constant.room;

/**
 * Validation and error message constants specific to the Room module.
 */
public final class RoomErrorMessageConstants {

    /**
     * Private constructor to prevent instantiation.
     */
    private RoomErrorMessageConstants() {}

    /**
     * Error message when room code is blank or null.
     */
    public static final String ROOM_CODE_NOT_BLANK = "Room code must not be blank";

    /**
     * Error message when room code exceeds maximum allowed length.
     */
    public static final String ROOM_CODE_MAX_LENGTH = "Room code must not exceed 20 characters";

    /**
     * Error message when room name is blank or null.
     */
    public static final String ROOM_NAME_NOT_BLANK = "Room name must not be blank";

    /**
     * Error message when room name exceeds maximum allowed length.
     */
    public static final String ROOM_NAME_MAX_LENGTH = "Room name must not exceed 100 characters";

    /**
     * Error message when capacity is null.
     */
    public static final String CAPACITY_NOT_NULL = "Capacity must not be null";

    /**
     * Error message when capacity is zero or negative.
     */
    public static final String CAPACITY_POSITIVE = "Capacity must be greater than zero";

    /**
     * Error message when capacity exceeds maximum allowed limit.
     */
    public static final String CAPACITY_MAX = "Capacity must not exceed 500";

    /**
     * Error message when room status is null.
     */
    public static final String STATUS_NOT_NULL = "Status must not be null";
}
