package com.vietsoftware.roommanagement.entity;

import com.vietsoftware.roommanagement.constant.CommonConstants;
import com.vietsoftware.roommanagement.constant.room.RoomErrorMessageConstants;
import com.vietsoftware.roommanagement.enums.RoomStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;

/**
 * Entity representing a room record in the database.
 */
@Entity
@Table(name = "rooms")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldNameConstants
public class Room extends BaseEntity {

    /**
     * Unique code of the room (e.g. R101).
     */
    @NotBlank(message = RoomErrorMessageConstants.ROOM_CODE_NOT_BLANK)
    @Size(max = CommonConstants.TWENTY, message = RoomErrorMessageConstants.ROOM_CODE_MAX_LENGTH)
    @Column(name = "room_code", nullable = false, unique = true, length = 20)
    String roomCode;

    /**
     * Name or title of the room.
     */
    @NotBlank(message = RoomErrorMessageConstants.ROOM_NAME_NOT_BLANK)
    @Size(max = CommonConstants.ONE_HUNDRED, message = RoomErrorMessageConstants.ROOM_NAME_MAX_LENGTH)
    @Column(nullable = false, length = 100, columnDefinition = "VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci")
    String name;

    /**
     * Maximum capacity (number of people) allowed in the room.
     */
    @NotNull(message = RoomErrorMessageConstants.CAPACITY_NOT_NULL)
    @Positive(message = RoomErrorMessageConstants.CAPACITY_POSITIVE)
    @Max(value = CommonConstants.FIVE_HUNDRED, message = RoomErrorMessageConstants.CAPACITY_MAX)
    @Column(nullable = false)
    Integer capacity;

    /**
     * Operational status of the room.
     */
    @NotNull(message = RoomErrorMessageConstants.STATUS_NOT_NULL)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    RoomStatus status;
}
