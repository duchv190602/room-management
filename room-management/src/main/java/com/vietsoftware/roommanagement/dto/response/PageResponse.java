package com.vietsoftware.roommanagement.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

/**
 * Standardized generic Data Transfer Object for paginated API response data payloads.
 *
 * @param <T> payload content item type
 */
@Schema(description = "Standardized pagination response payload wrapper")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PageResponse<T> {

    /**
     * List of content items in the current page.
     */
    @Schema(description = "Content items list for the requested page")
    List<T> content;

    /**
     * Current page number (0-indexed).
     */
    @Schema(description = "Current page index (0-indexed)", example = "0")
    int pageNo;

    /**
     * Number of items per page.
     */
    @Schema(description = "Page size limit", example = "10")
    int pageSize;

    /**
     * Total number of matching elements across all pages.
     */
    @Schema(description = "Total matching elements count", example = "42")
    long totalElements;

    /**
     * Total number of available pages.
     */
    @Schema(description = "Total available pages count", example = "5")
    int totalPages;

    /**
     * Flag indicating whether the current page is the last page.
     */
    @Schema(description = "Flag indicating if this is the last page", example = "false")
    boolean last;
}
