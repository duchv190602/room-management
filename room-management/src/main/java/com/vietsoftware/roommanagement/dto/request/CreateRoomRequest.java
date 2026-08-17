package com.vietsoftware.roommanagement.dto.request;

import com.vietsoftware.roommanagement.constant.CommonConstants;
import com.vietsoftware.roommanagement.constant.room.RoomErrorMessageConstants;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Data Transfer Object (DTO) for creating a new room request.
 */
@Schema(description = "Request payload for creating a new room")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRoomRequest {

    /**
     * Unique room code.
     */
    @Schema(description = "Unique code of the room", example = "ROOM-101", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = RoomErrorMessageConstants.ROOM_CODE_NOT_BLANK)
    @Size(max = CommonConstants.TWENTY, message = RoomErrorMessageConstants.ROOM_CODE_MAX_LENGTH)
    private String roomCode;

    /**
     * Room name.
     */
    @Schema(description = "Name or title of the room", example = "Conference Room A", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = RoomErrorMessageConstants.ROOM_NAME_NOT_BLANK)
    @Size(max = CommonConstants.ONE_HUNDRED, message = RoomErrorMessageConstants.ROOM_NAME_MAX_LENGTH)
    private String name;

    /**
     * Maximum capacity of the room.
     */
    @Schema(description = "Maximum capacity of people", example = "20", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = RoomErrorMessageConstants.CAPACITY_NOT_NULL)
    @Positive(message = RoomErrorMessageConstants.CAPACITY_POSITIVE)
    @Max(value = CommonConstants.FIVE_HUNDRED, message = RoomErrorMessageConstants.CAPACITY_MAX)
    private Integer capacity;
}
