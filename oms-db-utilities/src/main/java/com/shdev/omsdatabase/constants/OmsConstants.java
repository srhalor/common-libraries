package com.shdev.omsdatabase.constants;

import com.shdev.common.constants.HeaderConstants;
import lombok.experimental.UtilityClass;

/**
 * Constants used in the OMS database for user identification headers and tokens.
 * References common header constants for consistency.
 *
 * @author Shailesh Halor
 */
@UtilityClass
public class OmsConstants {

    /**
     * MDC key for user ID extracted from header
     */
    public static final String USER_ID_HEADER = HeaderConstants.MDC_USER_ID_HEADER;

    /**
     * MDC key for user ID extracted from JWT token
     */
    public static final String USER_ID_TOKEN = HeaderConstants.MDC_USER_ID_TOKEN;
}
