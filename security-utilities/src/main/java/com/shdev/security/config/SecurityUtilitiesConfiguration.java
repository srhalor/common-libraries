package com.shdev.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shdev.security.filter.JwtAuthenticationFilter;
import com.shdev.security.filter.OriginHeadersFilter;
import com.shdev.security.handler.CustomAccessDeniedHandler;
import com.shdev.security.handler.CustomAuthenticationEntryPoint;
import com.shdev.security.service.JwtValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration class for security utilities.
 * Provides necessary beans for JWT authentication and header validation.
 *
 * @author Shailesh Halor
 */
@Configuration
@EnableConfigurationProperties(SecurityFilterProperties.class)
@RequiredArgsConstructor
public class SecurityUtilitiesConfiguration {

    private final SecurityFilterProperties properties;

    /**
     * Provides RestTemplate bean if not already present.
     * Used for calling security-service token validation endpoint.
     */
    @Bean
    @ConditionalOnMissingBean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * Provides ObjectMapper bean if not already present.
     * Used for JSON serialization/deserialization.
     */
    @Bean
    @ConditionalOnMissingBean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    /**
     * Provides JwtValidationService bean.
     */
    @Bean
    public JwtValidationService jwtValidationService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        return new JwtValidationService(restTemplate, objectMapper);
    }

    /**
     * Provides JwtAuthenticationFilter bean.
     * This filter validates JWT tokens and sets Spring Security authentication context.
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtValidationService jwtValidationService) {
        return new JwtAuthenticationFilter(
                jwtValidationService,
                properties.getTokenValidationUrl(),
                properties.getExcludedPaths()
        );
    }

    /**
     * Provides OriginHeadersFilter bean.
     * This filter validates Atradius origin headers.
     */
    @Bean
    public OriginHeadersFilter originHeadersFilter() {
        return new OriginHeadersFilter(
                properties.getExcludedPaths(),
                properties.isStrictHeaderMode()
        );
    }

    /**
     * Provides CustomAccessDeniedHandler bean.
     * This handler returns proper JSON response for 403 Forbidden errors.
     * Note: The @Component annotation on the handler class itself won't work
     * because it needs ObjectMapper, so we create it here explicitly.
     */
    @Bean
    public CustomAccessDeniedHandler customAccessDeniedHandler(ObjectMapper objectMapper) {
        return new CustomAccessDeniedHandler();
    }

    /**
     * Provides CustomAuthenticationEntryPoint bean.
     * This handler returns proper JSON response for 401 Unauthorized errors.
     */
    @Bean
    public CustomAuthenticationEntryPoint customAuthenticationEntryPoint(ObjectMapper objectMapper) {
        return new CustomAuthenticationEntryPoint();
    }
}

