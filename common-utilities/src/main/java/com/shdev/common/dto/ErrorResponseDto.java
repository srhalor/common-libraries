package com.shdev.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Standard error response DTO using Java Record with builder pattern.
 *
 * @author Shailesh Halor
 */
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponseDto(
        @JsonProperty("timestamp") Instant timestamp,
        @JsonProperty("status") int status,
        @JsonProperty("error") String error,
        @JsonProperty("error_description") String errorDescription,
        @JsonProperty("message") String message,
        @JsonProperty("path") String path,
        @JsonProperty("errors") List<ValidationError> errors,
        @JsonProperty("metadata") Map<String, Object> metadata
) {
    /**
     * Validation error details.
     */
    @Builder
    public record ValidationError(
            String field,
            String message,
            Object rejectedValue
    ) {
    }
}

