package com.shdev.common.util;

import org.slf4j.MDC;

import java.util.Map;

/**
 * Utility class for Mapped Diagnostic Context (MDC) operations.
 * Provides convenient methods to manage MDC for structured logging and auditing.
 *
 * @author Shailesh Halor
 */
public final class MdcUtil {

    private MdcUtil() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Add a key-value pair to MDC.
     *
     * @param key   the MDC key
     * @param value the value to add
     */
    public static void put(String key, String value) {
        if (key != null && value != null) {
            MDC.put(key, value);
        }
    }

    /**
     * Get a value from MDC by key.
     *
     * @param key the MDC key
     * @return the value or null if not found
     */
    public static String get(String key) {
        return MDC.get(key);
    }

    /**
     * Remove a key from MDC.
     *
     * @param key the MDC key to remove
     */
    public static void remove(String key) {
        if (key != null) {
            MDC.remove(key);
        }
    }

    /**
     * Clear all MDC entries.
     */
    public static void clear() {
        MDC.clear();
    }

    /**
     * Add multiple entries to MDC.
     *
     * @param entries map of key-value pairs to add
     */
    public static void putAll(Map<String, String> entries) {
        if (entries != null && !entries.isEmpty()) {
            entries.forEach(MdcUtil::put);
        }
    }

    /**
     * Get all MDC entries as a map.
     *
     * @return map of all MDC entries
     */
    public static Map<String, String> getAll() {
        return MDC.getCopyOfContextMap();
    }

    /**
     * Execute a runnable with temporary MDC context.
     *
     * @param entries map of MDC entries to add temporarily
     * @param runnable the code to execute
     */
    public static void executeWithContext(Map<String, String> entries, Runnable runnable) {
        Map<String, String> originalContext = getAll();
        try {
            putAll(entries);
            runnable.run();
        } finally {
            clear();
            if (originalContext != null) {
                putAll(originalContext);
            }
        }
    }
}

