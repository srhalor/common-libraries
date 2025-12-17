package com.shdev.security.filter;

import com.shdev.common.constants.HeaderConstants;
import com.shdev.common.util.MdcUtil;
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

        if (isExcludedPath(path)) {
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
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("{\"error\":\"invalid_request\",\"error_description\":\"Missing required origin headers\"}");
                    return;
                }
            }

            if (StringUtils.hasText(originService)) {
                MdcUtil.put(HeaderConstants.MDC_ORIGIN_SERVICE, originService);
                log.debug("Added {} to MDC: {}", HeaderConstants.ATRADIUS_ORIGIN_SERVICE, originService);
            }

            if (StringUtils.hasText(originApplication)) {
                MdcUtil.put(HeaderConstants.MDC_ORIGIN_APPLICATION, originApplication);
                log.debug("Added {} to MDC: {}", HeaderConstants.ATRADIUS_ORIGIN_APPLICATION, originApplication);
            }

            if (StringUtils.hasText(originUser)) {
                MdcUtil.put(HeaderConstants.MDC_USER_ID_HEADER, originUser);
                log.debug("Added {} to MDC: {}", HeaderConstants.ATRADIUS_ORIGIN_USER, originUser);
            }

            log.info("✅ Origin Headers Filter - PASSED");
            filterChain.doFilter(request, response);

        } finally {
            MdcUtil.remove(HeaderConstants.MDC_ORIGIN_SERVICE);
            MdcUtil.remove(HeaderConstants.MDC_ORIGIN_APPLICATION);
            MdcUtil.remove(HeaderConstants.MDC_USER_ID_HEADER);
        }
    }

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

