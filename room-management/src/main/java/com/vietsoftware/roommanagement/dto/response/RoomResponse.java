package com.vietsoftware.roommanagement.dto.response;

import com.vietsoftware.roommanagement.enums.RoomStatus;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Data Transfer Object (DTO) representing room details in API responses.
 */
@Schema(description = "Response payload containing details of a room")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomResponse {

    /**
     * Unique identifier of the room.
     */
    @Schema(description = "Unique ID of the room", example = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11")
    private UUID id;

    /**
     * Unique room code.
     */
    @Schema(description = "Unique code of the room", example = "ROOM-101")
    private String roomCode;

    /**
     * Name or title of the room.
     */
    @Schema(description = "Name of the room", example = "Conference Room A")
    private String name;

    /**
     * Maximum capacity of the room.
     */
    @Schema(description = "Capacity of the room", example = "20")
    private Integer capacity;

    /**
     * Operational status of the room.
     */
    @Schema(description = "Operational status of the room", example = "ACTIVE")
    private RoomStatus status;

    /**
     * Creation timestamp.
     */
    @Schema(description = "Timestamp when room was created", example = "2026-08-05T10:00:00Z")
    private Instant createdAt;

    /**
     * Last update timestamp.
     */
    @Schema(description = "Timestamp when room was last updated", example = "2026-08-05T12:00:00Z")
    private Instant updatedAt;
}