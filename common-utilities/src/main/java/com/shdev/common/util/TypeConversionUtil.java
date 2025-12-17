package com.shdev.common.util;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * Utility class for type conversions and safe casting.
 *
 * @author Shailesh Halor
 */
@Slf4j
public final class TypeConversionUtil {

    private TypeConversionUtil() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Safely convert Object to Long.
     *
     * @param obj object to convert
     * @return Long value or null if conversion fails
     */
    public static Long toLong(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Number) {
            return ((Number) obj).longValue();
        }
        try {
            return Long.parseLong(obj.toString());
        } catch (NumberFormatException e) {
            log.warn("Failed to convert {} to Long", obj, e);
            return null;
        }
    }

    /**
     * Safely convert Object to Integer.
     *
     * @param obj object to convert
     * @return Integer value or null if conversion fails
     */
    public static Integer toInteger(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Number) {
            return ((Number) obj).intValue();
        }
        try {
            return Integer.parseInt(obj.toString());
        } catch (NumberFormatException e) {
            log.warn("Failed to convert {} to Integer", obj, e);
            return null;
        }
    }

    /**
     * Safely convert Object to String.
     *
     * @param obj object to convert
     * @return String value or null if object is null
     */
    public static String toString(Object obj) {
        return obj != null ? obj.toString() : null;
    }

    /**
     * Safely convert Object to List<String>.
     *
     * @param obj object to convert
     * @return List<String> or List containing the single string if not a list
     */
    @SuppressWarnings("unchecked")
    public static List<String> toStringList(Object obj) {
        if (obj == null) {
            return List.of();
        }
        if (obj instanceof List) {
            return (List<String>) obj;
        }
        return List.of(obj.toString());
    }

    /**
     * Safely convert Object to Map<String, Object>.
     *
     * @param obj object to convert
     * @return Map<String, Object> or empty map if conversion fails
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> toMap(Object obj) {
        if (obj == null) {
            return Map.of();
        }
        if (obj instanceof Map) {
            return (Map<String, Object>) obj;
        }
        log.warn("Failed to convert {} to Map", obj.getClass());
        return Map.of();
    }
}

