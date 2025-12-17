package com.shdev.security.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Map;

/**
 * Utility class for extracting user-friendly error messages from HTTP exceptions.
 * Particularly useful for parsing error responses from REST API calls.
 *
 * @author Shailesh Halor
 */
@Slf4j
public class ErrorMessageExtractor {

    /**
     * Extract user-friendly error message from HttpClientErrorException.
     * Attempts to parse JSON error response and extract error_description or error field.
     *
     * @param exception   the HttpClientErrorException
     * @param objectMapper ObjectMapper for JSON parsing
     * @return clean, user-friendly error message
     */
    public static String extractErrorMessage(HttpClientErrorException exception, ObjectMapper objectMapper) {
        try {
            String responseBody = exception.getResponseBodyAsString();

            // Try to parse JSON error response
            if (responseBody.contains("error_description") || responseBody.contains("error")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> errorResponse = objectMapper.readValue(responseBody, Map.class);

                // First, try to get error_description
                String errorDescription = (String) errorResponse.get("error_description");
                if (StringUtils.hasText(errorDescription)) {
                    return errorDescription;
                }

                // Fallback to error field
                String error = (String) errorResponse.get("error");
                if (StringUtils.hasText(error)) {
                    return formatErrorCode(error);
                }
            }

            // Fallback based on status code
            return HttpErrorStatus.getMessageForStatusCode(exception.getStatusCode().value());

        } catch (Exception parseException) {
            log.debug("Could not parse error response, using default message", parseException);
            return HttpErrorStatus.getMessageForStatusCode(exception.getStatusCode().value());
        }
    }

    /**
     * Format error code to readable message (e.g., "invalid_token" -> "Invalid token").
     */
    private static String formatErrorCode(String errorCode) {
        if (!StringUtils.hasText(errorCode)) {
            return "An error occurred";
        }

        // Replace underscores with spaces and capitalize first letter
        String formatted = errorCode.replace("_", " ");
        if (!formatted.isEmpty()) {
            formatted = formatted.substring(0, 1).toUpperCase() + formatted.substring(1);
        }
        return formatted;
    }
}

