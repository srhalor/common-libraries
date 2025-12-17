package com.shdev.security.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.shdev.common.dto.ErrorResponseDto;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.time.Instant;

/**
 * Enhanced utility class for sending standardized error responses from security filters.
 * Uses the common ErrorResponseDto for consistent error response format across all services.
 *
 * @author Shailesh Halor
 */
@Slf4j
public class SecurityErrorResponseUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private SecurityErrorResponseUtil() {
        // Utility class - prevent instantiation
    }

    /**
     * Send a standardized error response using ErrorResponseDto.
     *
     * @param response HttpServletResponse
     * @param status   HTTP status
     * @param error    Error type/code
     * @param message  Error message
     * @param path     Request path
     * @throws IOException if writing to response fails
     */
    public static void sendErrorResponse(
            HttpServletResponse response,
            HttpStatus status,
            String error,
            String message,
            String path) throws IOException {

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .error(error)
                .message(message)
                .path(path)
                .build();

        try {
            String jsonResponse = objectMapper.writeValueAsString(errorResponse);
            response.getWriter().write(jsonResponse);
            response.getWriter().flush();
        } catch (IOException e) {
            log.error("Failed to write error response", e);
            throw e;
        }
    }

    /**
     * Send unauthorized error (401).
     *
     * @param response HttpServletResponse
     * @param message  Error message
     * @param path     Request path
     * @throws IOException if writing to response fails
     */
    public static void sendUnauthorizedError(
            HttpServletResponse response,
            String message,
            String path) throws IOException {
        sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Unauthorized", message, path);
    }

    /**
     * Send bad request error (400).
     *
     * @param response HttpServletResponse
     * @param message  Error message
     * @param path     Request path
     * @throws IOException if writing to response fails
     */
    public static void sendBadRequestError(
            HttpServletResponse response,
            String message,
            String path) throws IOException {
        sendErrorResponse(response, HttpStatus.BAD_REQUEST, "Bad Request", message, path);
    }

    /**
     * Send forbidden error (403).
     *
     * @param response HttpServletResponse
     * @param message  Error message
     * @param path     Request path
     * @throws IOException if writing to response fails
     */
    public static void sendForbiddenError(
            HttpServletResponse response,
            String message,
            String path) throws IOException {
        sendErrorResponse(response, HttpStatus.FORBIDDEN, "Forbidden", message, path);
    }

    /**
     * Send internal server error (500).
     *
     * @param response HttpServletResponse
     * @param message  Error message
     * @param path     Request path
     * @throws IOException if writing to response fails
     */
    public static void sendInternalServerError(
            HttpServletResponse response,
            String message,
            String path) throws IOException {
        sendErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", message, path);
    }
}

