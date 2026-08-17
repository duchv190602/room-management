package com.vietsoftware.roommanagement.service;

import com.vietsoftware.roommanagement.dto.request.CreateRoomRequest;
import com.vietsoftware.roommanagement.dto.request.SearchRoomRequest;
import com.vietsoftware.roommanagement.dto.request.UpdateRoomRequest;
import com.vietsoftware.roommanagement.dto.response.PageResponse;
import com.vietsoftware.roommanagement.dto.response.RoomResponse;
import com.vietsoftware.roommanagement.enums.RoomStatus;
import com.vietsoftware.roommanagement.exception.AppException;

import java.util.UUID;

/**
 * Service interface defining business logic operations for room management.
 */
public interface IRoomService {

    /**
     * Searches and paginates active rooms matching filter criteria.
     * Force filters status to {@link RoomStatus#ACTIVE} regardless of input request status.
     *
     * @param searchRoomRequest filter and pagination parameters payload
     * @return {@link PageResponse} containing active {@link RoomResponse} page content
     */
    PageResponse<RoomResponse> searchActiveRooms(SearchRoomRequest searchRoomRequest);

    /**
     * Retrieves an active room by its UUID identifier.
     *
     * @param roomId unique room UUID identifier
     * @return {@link RoomResponse} containing details of the active room
     * @throws AppException if room is not found or status is not ACTIVE
     */
    RoomResponse getActiveRoomById(UUID roomId);

    /**
     * Searches and paginates all rooms across all operational statuses (ADMIN view).
     *
     * @param searchRoomRequest filter and pagination parameters payload (optional status filter)
     * @return {@link PageResponse} containing matching {@link RoomResponse} page content
     */
    PageResponse<RoomResponse> searchAllRooms(SearchRoomRequest searchRoomRequest);

    /**
     * Retrieves a room by its UUID identifier regardless of operational status (ADMIN view).
     *
     * @param roomId unique room UUID identifier
     * @return {@link RoomResponse} containing details of the room
     * @throws AppException if room is not found
     */
    RoomResponse getRoomById(UUID roomId);

    /**
     * Creates a new room entry with default ACTIVE operational status.
     *
     * @param createRoomRequest new room payload (roomCode, name, capacity)
     * @return {@link RoomResponse} containing details of the created room
     * @throws AppException if roomCode already exists
     */
    RoomResponse createRoom(CreateRoomRequest createRoomRequest);

    /**
     * Updates details of an existing room entry by its UUID identifier.
     *
     * @param roomId            unique room UUID identifier
     * @param updateRoomRequest update room payload
     * @return {@link RoomResponse} containing updated room details
     * @throws AppException if room is not found or new roomCode conflicts with another room
     */
    RoomResponse updateRoom(UUID roomId, UpdateRoomRequest updateRoomRequest);

    /**
     * Updates the operational status of an existing room entry.
     *
     * @param roomId unique room UUID identifier
     * @param status new target {@link RoomStatus} (ACTIVE or INACTIVE)
     * @return {@link RoomResponse} containing updated room details
     * @throws AppException if room is not found
     */
    RoomResponse updateRoomStatus(UUID roomId, RoomStatus status);

    /**
     * Soft deletes an existing room entry by updating its status to INACTIVE.
     *
     * @param roomId unique room UUID identifier
     * @throws AppException if room is not found
     */
    void deleteRoom(UUID roomId);
}
