package com.vietsoftware.roommanagement.dto.request;

import com.vietsoftware.roommanagement.constant.CommonConstants;
import com.vietsoftware.roommanagement.constant.room.RoomErrorMessageConstants;
import jakarta.validation.constraints.*;
import lombok.*;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Data Transfer Object (DTO) for updating an existing room request.
 */
@Schema(description = "Request payload for updating an existing room")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRoomRequest {

    /**
     * Updated room code.
     */
    @Schema(description = "Updated code of the room", example = "ROOM-101-UPDATED", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = RoomErrorMessageConstants.ROOM_CODE_NOT_BLANK)
    @Size(max = CommonConstants.TWENTY, message = RoomErrorMessageConstants.ROOM_CODE_MAX_LENGTH)
    private String roomCode;

    /**
     * Updated room name.
     */
    @Schema(description = "Updated name or title of the room", example = "Executive Conference Room A", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = RoomErrorMessageConstants.ROOM_NAME_NOT_BLANK)
    @Size(max = CommonConstants.ONE_HUNDRED, message = RoomErrorMessageConstants.ROOM_NAME_MAX_LENGTH)
    private String name;

    /**
     * Updated maximum capacity of the room.
     */
    @Schema(description = "Updated capacity of people", example = "25", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = RoomErrorMessageConstants.CAPACITY_NOT_NULL)
    @Positive(message = RoomErrorMessageConstants.CAPACITY_POSITIVE)
    @Max(value = CommonConstants.FIVE_HUNDRED, message = RoomErrorMessageConstants.CAPACITY_MAX)
    private Integer capacity;
}