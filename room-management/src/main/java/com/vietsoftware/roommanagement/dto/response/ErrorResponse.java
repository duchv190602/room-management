package com.vietsoftware.roommanagement.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Data Transfer Object (DTO) representing a standardized error response payload returned by the API.
 */
@Schema(description = "Standardized error response payload")
@Getter
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    /**
     * Business error code identifying the type of error.
     */
    @Schema(description = "Business error code", example = "ROOM_CODE_ALREADY_EXISTS")
    private final String code;

    /**
     * Human-readable error message describing the issue.
     */
    @Schema(description = "Human-readable error message", example = "Room code already exists")
    private final String message;

    /**
     * Timestamp when the error occurred.
     */
    @Schema(description = "Timestamp when the error occurred", example = "2026-08-06T02:18:12Z")
    private final Instant timestamp;

    /**
     * Map of validation errors for specific fields (if applicable).
     */
    @Schema(description = "Map of validation errors", nullable = true)
    private final Map<String, List<String>> errors;
}
