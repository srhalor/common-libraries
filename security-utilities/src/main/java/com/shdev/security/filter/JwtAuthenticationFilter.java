package com.shdev.security.filter;

import com.shdev.common.constants.HeaderConstants;
import com.shdev.common.util.HeaderValidator;
import com.shdev.common.util.MdcUtil;
import com.shdev.security.authentication.JwtAuthenticationToken;
import com.shdev.security.dto.TokenInfoDto;
import com.shdev.security.service.JwtValidationService;
import com.shdev.security.util.FilterErrorResponseUtil;
import com.shdev.security.util.RoleParser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * Filter to validate JWT token from Authorization header.
 * Validates token with security-service and extracts user roles.
 * Sets Spring Security context with authenticated user and authorities.
 *
 * @author Shailesh Halor
 */
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtValidationService jwtValidationService;
    private final String tokenValidationUrl;
    private final List<String> excludedPaths;

    /**
     * Constructor for JwtAuthenticationFilter.
     *
     * @param jwtValidationService Service for validating JWT tokens
     * @param tokenValidationUrl   security-service token validation endpoint URL
     * @param excludedPaths        paths to exclude from JWT validation
     */
    public JwtAuthenticationFilter(JwtValidationService jwtValidationService,
                                   String tokenValidationUrl,
                                   List<String> excludedPaths) {
        this.jwtValidationService = jwtValidationService;
        this.tokenValidationUrl = tokenValidationUrl;
        this.excludedPaths = excludedPaths;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();

        log.info("=== JWT Authentication Filter - START ===");
        log.info("Request: {} {}", method, path);

        if (isExcludedPath(path)) {
            log.info("Path '{}' is in excluded paths - SKIPPING JWT validation", path);
            filterChain.doFilter(request, response);
            return;
        }

        log.debug("Path '{}' NOT excluded - checking for JWT token", path);

        String authHeader = request.getHeader(HeaderConstants.AUTHORIZATION);
        String token = HeaderValidator.extractBearerToken(authHeader);

        // If no token present, return 401 Unauthorized
        if (token == null) {
            log.warn("❌ AUTHENTICATION REQUIRED - Missing JWT token for path: {}", path);
            FilterErrorResponseUtil.sendUnauthorizedError(
                    response,
                    "Authentication required. Please provide a valid JWT token.",
                    path);
            return;
        }

        log.debug("JWT token extracted successfully (length: {})", token.length());

        try {
            String identityDomain = request.getHeader(HeaderConstants.OAUTH_IDENTITY_DOMAIN_NAME);
            log.debug("Identity domain: {}", identityDomain);

            // Validate token with security-service
            TokenInfoDto tokenInfo = jwtValidationService.validateToken(token, identityDomain, tokenValidationUrl);
            log.info("✅ JWT token validated successfully");

            // Add token info to MDC for logging/auditing
            addToMdc(tokenInfo);

            // Extract roles from userRole field and set Spring Security context
            Collection<GrantedAuthority> authorities = RoleParser.parseAndConvertToAuthorities(tokenInfo.userRole());
            log.debug("Extracted {} authorities from userRole: {}", authorities.size(), tokenInfo.userRole());

            JwtAuthenticationToken authentication = new JwtAuthenticationToken(tokenInfo.subject(), authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("Set Spring Security authentication context");

            log.info("=== JWT Authentication Filter - PASSED ===");
            filterChain.doFilter(request, response);

        } catch (JwtValidationService.TokenValidationException e) {
            log.error("❌ AUTHENTICATION FAILED - JWT validation failed for path: {}", path);
            log.error("Error details: {}", e.getMessage());
            SecurityContextHolder.clearContext();
            FilterErrorResponseUtil.sendUnauthorizedError(
                    response,
                    "Token validation failed: " + e.getMessage(),
                    path);
        } finally {
            clearMdc();
        }
    }

    /**
     * Add token information to MDC for logging and auditing.
     */
    private void addToMdc(TokenInfoDto tokenInfo) {
        if (StringUtils.hasText(tokenInfo.subject())) {
            MdcUtil.put(HeaderConstants.MDC_USER_ID_TOKEN, tokenInfo.subject());
            log.debug("Added subject to MDC: {}", tokenInfo.subject());
        }

        if (StringUtils.hasText(tokenInfo.client())) {
            MdcUtil.put(HeaderConstants.MDC_CLIENT, tokenInfo.client());
            log.debug("Added client to MDC: {}", tokenInfo.client());
        }

        if (StringUtils.hasText(tokenInfo.domain())) {
            MdcUtil.put(HeaderConstants.MDC_DOMAIN, tokenInfo.domain());
            log.debug("Added domain to MDC: {}", tokenInfo.domain());
        }
    }

    /**
     * Clear MDC values set by this filter.
     */
    private void clearMdc() {
        MdcUtil.remove(HeaderConstants.MDC_USER_ID_TOKEN);
        MdcUtil.remove(HeaderConstants.MDC_CLIENT);
        MdcUtil.remove(HeaderConstants.MDC_DOMAIN);
    }

    /**
     * Check if the given path should be excluded from JWT validation.
     */

    private boolean isExcludedPath(String path) {
        return excludedPaths.stream().anyMatch(pattern -> pathMatches(path, pattern));
    }

    private boolean pathMatches(String path, String pattern) {
        if (pattern.endsWith("/**")) {
            String prefix = pattern.substring(0, pattern.length() - 3);
            return path.startsWith(prefix);
        }
        return path.equals(pattern) || path.startsWith(pattern + "/");
    }

}

