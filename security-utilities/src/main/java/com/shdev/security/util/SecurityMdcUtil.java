package com.shdev.security.util;

import com.shdev.common.constants.HeaderConstants;
import com.shdev.common.util.MdcUtil;
import com.shdev.security.dto.TokenInfoDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/**
 * Utility class for managing security-related MDC (Mapped Diagnostic Context) operations.
 * Provides convenient methods to add/remove security context information to/from MDC for logging.
 *
 * @author Shailesh Halor
 */
@Slf4j
public class SecurityMdcUtil {

    private SecurityMdcUtil() {
        // Utility class - prevent instantiation
    }

    /**
     * Add token information to MDC for logging and auditing.
     *
     * @param tokenInfo the token information DTO
     */
    public static void addTokenInfoToMdc(TokenInfoDto tokenInfo) {
        if (tokenInfo == null) {
            log.debug("TokenInfo is null, skipping MDC update");
            return;
        }

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
     * Remove token-related information from MDC.
     * Should be called in finally block to ensure cleanup.
     */
    public static void clearTokenInfoFromMdc() {
        MdcUtil.remove(HeaderConstants.MDC_USER_ID_TOKEN);
        MdcUtil.remove(HeaderConstants.MDC_CLIENT);
        MdcUtil.remove(HeaderConstants.MDC_DOMAIN);
    }

    /**
     * Add origin headers to MDC for logging and auditing.
     *
     * @param originService     the Atradius-Origin-Service header value
     * @param originApplication the Atradius-Origin-Application header value
     * @param originUser        the Atradius-Origin-User header value
     */
    public static void addOriginHeadersToMdc(String originService, String originApplication, String originUser) {
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
    }

    /**
     * Remove origin header information from MDC.
     * Should be called in finally block to ensure cleanup.
     */
    public static void clearOriginHeadersFromMdc() {
        MdcUtil.remove(HeaderConstants.MDC_ORIGIN_SERVICE);
        MdcUtil.remove(HeaderConstants.MDC_ORIGIN_APPLICATION);
        MdcUtil.remove(HeaderConstants.MDC_USER_ID_HEADER);
    }

    /**
     * Clear all security-related MDC values.
     * Convenience method that clears both token and origin header information.
     */
    public static void clearAllSecurityMdc() {
        clearTokenInfoFromMdc();
        clearOriginHeadersFromMdc();
    }
}

