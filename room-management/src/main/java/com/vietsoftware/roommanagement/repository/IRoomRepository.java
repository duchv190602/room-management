package com.vietsoftware.roommanagement.repository;

import com.vietsoftware.roommanagement.entity.Room;
import com.vietsoftware.roommanagement.enums.RoomStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA Repository interface for performing persistence operations on {@link Room} entities.
 */
@Repository
public interface IRoomRepository extends JpaRepository<Room, UUID>, JpaSpecificationExecutor<Room> {

    /**
     * Checks if a room record exists with the given room code.
     *
     * @param roomCode unique room code to check
     * @return {@code true} if a room with the code exists, {@code false} otherwise
     */
    boolean existsByRoomCode(String roomCode);

    /**
     * Checks if a room record exists with the given room code, excluding a specific room ID.
     *
     * @param roomCode unique room code to check
     * @param id       room ID to exclude from existence check
     * @return {@code true} if another room with the same code exists, {@code false} otherwise
     */
    boolean existsByRoomCodeAndIdNot(String roomCode, UUID id);

    /**
     * Finds a room by ID only if it matches the specified operational status.
     * Combines existence and status validation in a single database query.
     *
     * @param id     room UUID
     * @param status required operational status
     * @return an {@link Optional} containing the matching room, or empty if not found or status mismatch
     */
    Optional<Room> findByIdAndStatus(UUID id, RoomStatus status);
}
