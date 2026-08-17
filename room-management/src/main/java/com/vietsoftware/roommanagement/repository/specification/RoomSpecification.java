package com.vietsoftware.roommanagement.repository.specification;

import com.vietsoftware.roommanagement.constant.CommonConstants;
import com.vietsoftware.roommanagement.entity.Room;
import com.vietsoftware.roommanagement.enums.RoomStatus;
import org.springframework.data.jpa.domain.Specification;

/**
 * Reusable JPA Specifications for querying {@link Room} entities dynamically.
 */
public final class RoomSpecification {

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private RoomSpecification() {}

    /**
     * Specification to filter rooms by exact status equality.
     *
     * @param status required room status filter, or null to omit status filtering
     * @return {@link Specification} checking status equality, or conjunction if status is null
     */
    public static Specification<Room> hasStatus(RoomStatus status) {
        return (root, query, cb) -> {
            if (status == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get(Room.Fields.status), status);
        };
    }

    /**
     * Specification to filter rooms by exact room code match.
     *
     * @param roomCode room code keyword
     * @return {@link Specification} checking roomCode equality, or conjunction if roomCode is null/blank
     */
    public static Specification<Room> hasRoomCode(String roomCode) {
        return (root, query, cb) -> {
            if (roomCode == null || roomCode.trim().isEmpty()) {
                return cb.conjunction();
            }
            return cb.equal(root.get(Room.Fields.roomCode), roomCode.trim());
        };
    }

    /**
     * Specification to filter rooms by case-insensitive name partial match (LIKE).
     *
     * @param name room name keyword
     * @return {@link Specification} checking name containing keyword, or conjunction if name is null/blank
     */
    public static Specification<Room> hasNameLike(String name) {
        return (root, query, cb) -> {
            if (name == null || name.trim().isEmpty()) {
                return cb.conjunction();
            }
            return cb.like(
                    cb.lower(root.get(Room.Fields.name)),
                    CommonConstants.LIKE_WILDCARD + name.trim().toLowerCase() + CommonConstants.LIKE_WILDCARD
            );
        };
    }

    /**
     * Specification to filter rooms by exact capacity match.
     *
     * @param capacity capacity value filter
     * @return {@link Specification} checking capacity equality, or conjunction if capacity is null
     */
    public static Specification<Room> hasCapacity(Integer capacity) {
        return (root, query, cb) -> {
            if (capacity == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get(Room.Fields.capacity), capacity);
        };
    }
}
