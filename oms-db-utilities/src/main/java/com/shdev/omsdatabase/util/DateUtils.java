package com.shdev.omsdatabase.util;

import lombok.experimental.UtilityClass;

import java.time.OffsetDateTime;

/**
 * Utility class for date-related operations.
 *
 * @author Shailesh Halor
 */
@UtilityClass
public class DateUtils {

    /**
     * Returns the Oracle database maximum date '4712-12-31'.
     *
     * @return OffsetDateTime representing '4712-12-31'
     */
    public static OffsetDateTime oracleEndDate() {
        return OffsetDateTime.parse("4712-12-31");
    }

}
