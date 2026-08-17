package com.vietsoftware.roommanagement.dto.request;

import com.vietsoftware.roommanagement.constant.ApiConstants;
import com.vietsoftware.roommanagement.enums.RoomStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * Data Transfer Object (DTO) payload for searching and filtering room records with pagination parameters.
 */
@Schema(description = "Criteria parameters payload for searching and paginating rooms")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SearchRoomRequest {

    /**
     * Optional room code query filter keyword.
     */
    @Schema(description = "Filter by room code keyword", example = "ROOM-101")
    String roomCode;

    /**
     * Optional room name query filter keyword.
     */
    @Schema(description = "Filter by room name keyword", example = "Conference")
    String name;

    /**
     * Optional capacity query filter.
     */
    @Schema(description = "Filter by capacity", example = "10")
    Integer capacity;

    /**
     * Optional status filter (applicable for ADMIN searches, ignored for regular USER active searches).
     */
    @Schema(description = "Filter by operational status (ADMIN only)", example = "ACTIVE")
    RoomStatus status;

    /**
     * Page number index (0-indexed). Defaults to 0 if null.
     */
    @Min(value = 0, message = "Page index must be greater than or equal to 0")
    @Schema(description = "Page index (0-indexed)", example = ApiConstants.DEFAULT_PAGE_NUMBER)
    @Builder.Default
    Integer pageNo = 0;

    /**
     * Page size limit. Defaults to 10 if null.
     */
    @Min(value = 1, message = "Page size must be at least 1")
    @Max(value = 100, message = "Page size cannot exceed 100")
    @Schema(description = "Page size limit", example = ApiConstants.DEFAULT_PAGE_SIZE)
    @Builder.Default
    Integer pageSize = 10;

}