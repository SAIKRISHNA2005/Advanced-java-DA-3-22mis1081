package com.example.onlinecourse.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class for date formatting.
 */
public class DateFormatter {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMM dd, yyyy");
    private static final SimpleDateFormat DATETIME_FORMAT = new SimpleDateFormat("MMM dd, yyyy HH:mm");

    /**
     * Format date to readable string.
     *
     * @param date Date to format
     * @return Formatted date string
     */
    public static String formatDate(Date date) {
        if (date == null) {
            return "";
        }
        return DATE_FORMAT.format(date);
    }

    /**
     * Format date-time to readable string.
     *
     * @param date Date to format
     * @return Formatted date-time string
     */
    public static String formatDateTime(Date date) {
        if (date == null) {
            return "";
        }
        return DATETIME_FORMAT.format(date);
    }
}

