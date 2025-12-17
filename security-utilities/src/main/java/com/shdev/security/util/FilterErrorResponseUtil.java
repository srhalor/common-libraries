package com.shdev.security.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for sending standardized error responses from filters.
 * Ensures consistent error response format across all security filters.
 *
 * @author Shailesh Halor
 */
@Slf4j
public class FilterErrorResponseUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Send a standardized error response.
     *
     * @param response    HttpServletResponse
     * @param status      HTTP status code
     * @param error       Error code
     * @param message     Error message
     * @param path        Request path
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

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        errorResponse.put("status", status.value());
        errorResponse.put("error", error);
        errorResponse.put("message", message);
        errorResponse.put("path", path);

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
     */
    public static void sendUnauthorizedError(
            HttpServletResponse response,
            String message,
            String path) throws IOException {
        sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Unauthorized", message, path);
    }

    /**
     * Send bad request error (400).
     */
    public static void sendBadRequestError(
            HttpServletResponse response,
            String message,
            String path) throws IOException {
        sendErrorResponse(response, HttpStatus.BAD_REQUEST, "Bad Request", message, path);
    }

    /**
     * Send forbidden error (403).
     */
    public static void sendForbiddenError(
            HttpServletResponse response,
            String message,
            String path) throws IOException {
        sendErrorResponse(response, HttpStatus.FORBIDDEN, "Forbidden", message, path);
    }
}

