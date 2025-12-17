package com.shdev.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shdev.security.filter.JwtAuthenticationFilter;
import com.shdev.security.filter.OriginHeadersFilter;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Autoconfiguration for security filters.
 *
 * @author Shailesh Halor
 */
@Slf4j
@Configuration
public class SecurityFilterAutoConfiguration {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    @ConfigurationProperties(prefix = "security.filter")
    public SecurityFilterProperties securityFilterProperties() {
        return new SecurityFilterProperties();
    }

    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtAuthenticationFilter(
            SecurityFilterProperties properties,
            RestTemplate restTemplate,
            ObjectMapper objectMapper) {

        log.info("====== JWT Authentication Filter Configuration ======");
        log.info("Filter enabled: {}", properties.isJwtEnabled());
        log.info("Token validation URL: {}", properties.getTokenValidationUrl());
        log.info("URL patterns: {}", properties.getUrlPatterns());
        log.info("Excluded paths: {}", properties.getExcludedPaths());
        log.info("Filter order: 1");

        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(
                properties.getTokenValidationUrl(),
                restTemplate,
                objectMapper,
                properties.getExcludedPaths()
        );

        FilterRegistrationBean<JwtAuthenticationFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(filter);
        // Apply to all requests - filter will use internal path matching
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(1);
        registrationBean.setEnabled(properties.isJwtEnabled());

        log.info("JWT Authentication Filter registered successfully");
        log.info("Actual URL pattern used: /* (filter handles internal path matching)");
        log.info("====================================================");

        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<OriginHeadersFilter> originHeadersFilter(SecurityFilterProperties properties) {

        log.info("====== Origin Headers Filter Configuration ======");
        log.info("Filter enabled: {}", properties.isHeadersEnabled());
        log.info("Strict mode: {}", properties.isStrictHeaderMode());
        log.info("URL patterns: {}", properties.getUrlPatterns());
        log.info("Excluded paths: {}", properties.getExcludedPaths());
        log.info("Filter order: 2");

        OriginHeadersFilter filter = new OriginHeadersFilter(
                properties.getExcludedPaths(),
                properties.isStrictHeaderMode()
        );

        FilterRegistrationBean<OriginHeadersFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(filter);
        // Apply to all requests - filter will use internal path matching
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(2);
        registrationBean.setEnabled(properties.isHeadersEnabled());

        log.info("Origin Headers Filter registered successfully");
        log.info("Actual URL pattern used: /* (filter handles internal path matching)");
        log.info("================================================");

        return registrationBean;
    }

    @Data
    public static class SecurityFilterProperties {
        private boolean jwtEnabled = true;
        private boolean headersEnabled = true;
        private boolean strictHeaderMode = false;
        private String tokenValidationUrl = "http://localhost:8090/oauth2/rest/token/info";
        private List<String> urlPatterns = List.of("/api/*");
        private List<String> excludedPaths = List.of("/actuator/**", "/api/health");
    }
}

