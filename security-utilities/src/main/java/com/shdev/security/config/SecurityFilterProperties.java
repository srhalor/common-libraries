package com.shdev.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration properties for security filters.
 * These properties are used when adding filters to Spring Security.
 *
 * @author Shailesh Halor
 */
@Data
@Component
@ConfigurationProperties(prefix = "security.filter")
public class SecurityFilterProperties {

    /**
     * Security-service token validation endpoint URL.
     */
    private String tokenValidationUrl = "http://localhost:8090/oauth2/rest/token/info";

    /**
     * Paths to exclude from JWT and header validation.
     */
    private List<String> excludedPaths = new ArrayList<>();

    /**
     * Whether to enforce strict header validation (require all Atradius headers).
     */
    private boolean strictHeaderMode = true;
}

