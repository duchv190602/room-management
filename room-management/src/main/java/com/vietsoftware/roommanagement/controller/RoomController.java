package com.vietsoftware.roommanagement.controller;

import com.vietsoftware.roommanagement.constant.ApiConstants;
import com.vietsoftware.roommanagement.dto.request.CreateRoomRequest;
import com.vietsoftware.roommanagement.dto.request.SearchRoomRequest;
import com.vietsoftware.roommanagement.dto.request.UpdateRoomRequest;
import com.vietsoftware.roommanagement.dto.response.PageResponse;
import com.vietsoftware.roommanagement.dto.response.RoomResponse;
import com.vietsoftware.roommanagement.enums.RoomStatus;
import com.vietsoftware.roommanagement.service.IRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST Controller providing room management endpoints for searching, retrieving, creating, updating, changing status, and deleting rooms.
 */
@Tag(name = "Room Management", description = "Endpoints for managing and searching room resources")
@RestController
@RequestMapping(ApiConstants.ROOM_PATH)
@RequiredArgsConstructor
public class RoomController {

    private final IRoomService roomService;

    /**
     * Searches and paginates active rooms (accessible by USER and ADMIN).
     *
     * @param searchRoomRequest search criteria and pagination payload (pageNo, pageSize, sortBy, sortDir, roomCode, name, capacity)
     * @return {@link ResponseEntity} wrapping {@link PageResponse} of active {@link RoomResponse} objects and HTTP 200 status
     */
    @Operation(summary = "Search active rooms (User & Admin)",
            description = "Searches and paginates ACTIVE rooms matching criteria. Force filters status to ACTIVE.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved paginated active rooms list"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated - Token missing or invalid"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Lacks permission")
    })
    @GetMapping("/active")
    public ResponseEntity<PageResponse<RoomResponse>> searchActiveRooms(@Valid SearchRoomRequest searchRoomRequest) {
        PageResponse<RoomResponse> response = roomService.searchActiveRooms(searchRoomRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves active room details by ID (accessible by USER and ADMIN).
     *
     * @param roomId unique room UUID identifier
     * @return {@link ResponseEntity} containing active {@link RoomResponse} and HTTP 200 status
     */
    @Operation(summary = "Get active room by ID (User & Admin)",
            description = "Retrieves active room details by UUID. Returns 404 if room is INACTIVE or does not exist.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved active room details"),
            @ApiResponse(responseCode = "404", description = "Room not found or status is INACTIVE")
    })
    @GetMapping("/active/{roomId}")
    public ResponseEntity<RoomResponse> getActiveRoomById(
            @Parameter(description = "UUID identifier of the room", required = true)
            @PathVariable UUID roomId) {
        RoomResponse response = roomService.getActiveRoomById(roomId);
        return ResponseEntity.ok(response);
    }

    /**
     * Searches and paginates all rooms across all operational statuses (ADMIN only).
     *
     * @param searchRoomRequest search criteria and pagination payload (pageNo, pageSize, sortBy, sortDir, status, roomCode, name, capacity)
     * @return {@link ResponseEntity} wrapping {@link PageResponse} of matching {@link RoomResponse} objects and HTTP 200 status
     */
    @Operation(summary = "Search all rooms (Admin only)",
            description = "Searches and paginates all rooms across all operational statuses (ACTIVE, INACTIVE).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved paginated all rooms list"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role")
    })
    @GetMapping
    public ResponseEntity<PageResponse<RoomResponse>> searchAllRooms(@Valid SearchRoomRequest searchRoomRequest) {
        PageResponse<RoomResponse> response = roomService.searchAllRooms(searchRoomRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves room details by ID regardless of status (ADMIN only).
     *
     * @param roomId unique room UUID identifier
     * @return {@link ResponseEntity} containing {@link RoomResponse} and HTTP 200 status
     */
    @Operation(summary = "Get room by ID (Admin only)",
            description = "Retrieves specific room details by UUID regardless of status.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved room details"),
            @ApiResponse(responseCode = "404", description = "Room not found")
    })
    @GetMapping("/{roomId}")
    public ResponseEntity<RoomResponse> getRoomById(
            @Parameter(description = "UUID identifier of the room", required = true)
            @PathVariable UUID roomId) {
        RoomResponse response = roomService.getRoomById(roomId);
        return ResponseEntity.ok(response);
    }

    /**
     * Creates a new room entry with default ACTIVE operational status (ADMIN only).
     *
     * @param createRoomRequest payload containing new room details
     * @return {@link ResponseEntity} with created {@link RoomResponse} and HTTP 201 status
     */
    @Operation(summary = "Create a new room (Admin only)",
            description = "Creates a new room entry. Status defaults to ACTIVE.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Room successfully created"),
            @ApiResponse(responseCode = "400", description = "Validation error on payload"),
            @ApiResponse(responseCode = "409", description = "Conflict - Room code already exists")
    })
    @PostMapping
    public ResponseEntity<RoomResponse> createRoom(@Valid @RequestBody CreateRoomRequest createRoomRequest) {
        RoomResponse response = roomService.createRoom(createRoomRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Updates details of an existing room entry by UUID (ADMIN only).
     *
     * @param roomId            unique room UUID identifier
     * @param updateRoomRequest payload containing updated room details
     * @return {@link ResponseEntity} with updated {@link RoomResponse} and HTTP 200 status
     */
    @Operation(summary = "Update an existing room (Admin only)",
            description = "Updates room details (roomCode, name, capacity) by UUID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Room successfully updated"),
            @ApiResponse(responseCode = "400", description = "Validation error on payload"),
            @ApiResponse(responseCode = "404", description = "Room not found"),
            @ApiResponse(responseCode = "409", description = "Conflict - Room code already exists on another room")
    })
    @PutMapping("/{roomId}")
    public ResponseEntity<RoomResponse> updateRoom(
            @Parameter(description = "UUID identifier of the room", required = true)
            @PathVariable UUID roomId,
            @Valid @RequestBody UpdateRoomRequest updateRoomRequest) {
        RoomResponse response = roomService.updateRoom(roomId, updateRoomRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Updates the operational status of an existing room entry (ADMIN only).
     *
     * @param roomId unique room UUID identifier
     * @param status target {@link RoomStatus} (ACTIVE or INACTIVE)
     * @return {@link ResponseEntity} with updated {@link RoomResponse} and HTTP 200 status
     */
    @Operation(summary = "Update room status (Admin only)",
            description = "Updates the operational status of a room (ACTIVE or INACTIVE).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Room status successfully updated"),
            @ApiResponse(responseCode = "404", description = "Room not found")
    })
    @PatchMapping("/{roomId}/status")
    public ResponseEntity<RoomResponse> updateRoomStatus(
            @Parameter(description = "UUID identifier of the room", required = true)
            @PathVariable UUID roomId,
            @Parameter(description = "New target operational status", required = true)
            @RequestParam RoomStatus status) {
        RoomResponse response = roomService.updateRoomStatus(roomId, status);
        return ResponseEntity.ok(response);
    }

    /**
     * Soft deletes a room entry by setting its status to INACTIVE (ADMIN only).
     *
     * @param roomId unique room UUID identifier
     * @return {@link ResponseEntity} with empty body and HTTP 200 status
     */
    @Operation(summary = "Soft delete a room (Admin only)",
            description = "Soft deletes a room by setting its operational status to INACTIVE.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Room successfully soft deleted"),
            @ApiResponse(responseCode = "404", description = "Room not found")
    })
    @DeleteMapping("/{roomId}")
    public ResponseEntity<Void> deleteRoom(
            @Parameter(description = "UUID identifier of the room to delete", required = true)
            @PathVariable UUID roomId) {
        roomService.deleteRoom(roomId);
        return ResponseEntity.ok().build();
    }
}
