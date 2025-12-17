package com.shdev.security.filter;

import com.shdev.common.constants.HeaderConstants;
import com.shdev.security.authentication.JwtAuthenticationToken;
import com.shdev.security.constants.SecurityConstants;
import com.shdev.security.dto.TokenInfoDto;
import com.shdev.security.exception.TokenValidationException;
import com.shdev.security.service.JwtValidationService;
import com.shdev.security.util.JwtTokenUtil;
import com.shdev.security.util.SecurityErrorResponseUtil;
import com.shdev.security.util.SecurityMdcUtil;
import com.shdev.security.util.PathMatcher;
import com.shdev.security.util.RoleParser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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

        if (PathMatcher.isExcluded(path, excludedPaths)) {
            log.info("Path '{}' is in excluded paths - SKIPPING JWT validation", path);
            filterChain.doFilter(request, response);
            return;
        }

        log.debug("Path '{}' NOT excluded - checking for JWT token", path);

        String authHeader = request.getHeader(HeaderConstants.AUTHORIZATION);
        String token = JwtTokenUtil.extractBearerToken(authHeader);

        // If no token present, return 401 Unauthorized
        if (token == null) {
            log.warn("❌ AUTHENTICATION REQUIRED - Missing JWT token for path: {}", path);
            SecurityErrorResponseUtil.sendUnauthorizedError(
                    response,
                    SecurityConstants.ERROR_MISSING_TOKEN,
                    path);
            return;
        }

        log.debug("JWT token extracted successfully (length: {})", token.length());

        // Validate JWT format before calling security-service
        if (!JwtTokenUtil.hasValidJwtFormat(token)) {
            log.warn("❌ INVALID TOKEN FORMAT - Token does not match JWT structure for path: {}", path);
            SecurityErrorResponseUtil.sendUnauthorizedError(
                    response,
                    SecurityConstants.ERROR_INVALID_TOKEN_FORMAT,
                    path);
            return;
        }

        log.debug("JWT token format validated successfully");

        try {
            String identityDomain = request.getHeader(HeaderConstants.OAUTH_IDENTITY_DOMAIN_NAME);
            log.debug("Identity domain: {}", identityDomain);

            // Validate token with security-service
            TokenInfoDto tokenInfo = jwtValidationService.validateToken(token, identityDomain, tokenValidationUrl);
            log.info("✅ JWT token validated successfully");

            // Add token info to MDC for logging/auditing
            SecurityMdcUtil.addTokenInfoToMdc(tokenInfo);

            // Extract roles from userRole field and set Spring Security context
            Collection<GrantedAuthority> authorities = RoleParser.parseAndConvertToAuthorities(tokenInfo.userRole());
            log.debug("Extracted {} authorities from userRole: {}", authorities.size(), tokenInfo.userRole());

            JwtAuthenticationToken authentication = new JwtAuthenticationToken(tokenInfo.subject(), authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("Set Spring Security authentication context");

            log.info("=== JWT Authentication Filter - PASSED ===");
            filterChain.doFilter(request, response);

        } catch (TokenValidationException e) {
            log.error("❌ AUTHENTICATION FAILED - JWT validation failed for path: {}", path);
            log.error("Error details: {}", e.getMessage());
            SecurityContextHolder.clearContext();
            SecurityErrorResponseUtil.sendUnauthorizedError(
                    response,
                    e.getMessage(),
                    path);
        } finally {
            SecurityMdcUtil.clearTokenInfoFromMdc();
        }
    }

}
