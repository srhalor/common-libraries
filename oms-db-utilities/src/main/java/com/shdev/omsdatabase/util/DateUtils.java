package com.shdev.omsdatabase.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

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
     * @return LocalDate representing '4712-12-31'
     */
    public static LocalDate oracleEndDate() {
        return LocalDate.parse("4712-12-31");
    }

}
