package com.shdev.common.constants;

import lombok.experimental.UtilityClass;

/**
 * Constants for standard HTTP headers used across services.
 *
 * @author Shailesh Halor
 */
@UtilityClass
public class HeaderConstants {

    /**
     * Custom Atradius origin headers
     */
    public static final String ATRADIUS_ORIGIN_SERVICE = "Atradius-Origin-Service";
    public static final String ATRADIUS_ORIGIN_APPLICATION = "Atradius-Origin-Application";
    public static final String ATRADIUS_ORIGIN_USER = "Atradius-Origin-User";

    /**
     * OAuth2 headers
     */
    public static final String OAUTH_IDENTITY_DOMAIN_NAME = "X-OAUTH-IDENTITY-DOMAIN-NAME";
    public static final String AUTHORIZATION = "Authorization";

    /**
     * MDC keys for logging context
     */
    public static final String MDC_ORIGIN_SERVICE = "originService";
    public static final String MDC_ORIGIN_APPLICATION = "originApplication";
    public static final String MDC_ORIGIN_USER = "originUser";
    public static final String MDC_USER_ID_TOKEN = "userIdToken";
    public static final String MDC_USER_ID_HEADER = "userIdHeader";
    public static final String MDC_CLIENT = "client";
    public static final String MDC_DOMAIN = "domain";
    public static final String MDC_REQUEST_ID = "requestId";
}

