package com.shdev.security.filter;

import com.shdev.common.constants.HeaderConstants;
import com.shdev.security.constants.SecurityConstants;
import com.shdev.security.util.SecurityErrorResponseUtil;
import com.shdev.security.util.SecurityMdcUtil;
import com.shdev.security.util.PathMatcher;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Filter to validate and add Atradius origin headers to MDC for structured logging and auditing.
 *
 * @author Shailesh Halor
 */
@Slf4j
public class OriginHeadersFilter extends OncePerRequestFilter {


    private final List<String> excludedPaths;
    private final boolean strictMode;

    public OriginHeadersFilter(List<String> excludedPaths, boolean strictMode) {
        this.excludedPaths = excludedPaths;
        this.strictMode = strictMode;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();

        log.info("=== Origin Headers Filter - START ===");
        log.info("Request: {} {}", method, path);

        if (PathMatcher.isExcluded(path, excludedPaths)) {
            log.info("Path '{}' is in excluded paths - SKIPPING header validation", path);
            filterChain.doFilter(request, response);
            return;
        }

        log.debug("Path '{}' NOT excluded - Header validation REQUIRED", path);

        try {
            String originService = request.getHeader(HeaderConstants.ATRADIUS_ORIGIN_SERVICE);
            String originApplication = request.getHeader(HeaderConstants.ATRADIUS_ORIGIN_APPLICATION);
            String originUser = request.getHeader(HeaderConstants.ATRADIUS_ORIGIN_USER);

            log.debug("Strict mode: {}", strictMode);
            log.debug("Origin headers - Service: {}, Application: {}, User: {}",
                     originService != null, originApplication != null, originUser != null);

            if (strictMode) {
                if (!StringUtils.hasText(originService) ||
                    !StringUtils.hasText(originApplication) ||
                    !StringUtils.hasText(originUser)) {

                    log.warn("❌ HEADER VALIDATION FAILED - Missing required origin headers for path: {}", path);
                    log.warn("Headers: Service={}, Application={}, User={}",
                            originService != null, originApplication != null, originUser != null);

                    SecurityErrorResponseUtil.sendBadRequestError(
                            response,
                            SecurityConstants.ERROR_MISSING_ORIGIN_HEADERS,
                            path);
                    return;
                }
            }

            // Add origin headers to MDC
            SecurityMdcUtil.addOriginHeadersToMdc(originService, originApplication, originUser);

            log.info("✅ Origin Headers Filter - PASSED");
            filterChain.doFilter(request, response);

        } finally {
            SecurityMdcUtil.clearOriginHeadersFromMdc();
        }
    }

}

