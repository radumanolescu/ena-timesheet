package com.ena.timesheet.util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;

public class Time {
    public static final DateTimeFormatter isoLocalDateFmt = DateTimeFormatter.ISO_LOCAL_DATE;
    public static final DateTimeFormatter yyyyMMFmt = DateTimeFormatter.ofPattern("yyyyMM");
    public static final DateTimeFormatter mmyyFmt = DateTimeFormatter.ofPattern("MMMM yyyy");

    public static Float hoursBetween(LocalTime start, LocalTime end) {
        if (start == null || end == null) return null;
        long minutes = ChronoUnit.MINUTES.between(start, end); // Calculate the difference in minutes
        return minutes / 60.0f; // Calculate the difference in hours
    }

    public static int weekOfMonth(LocalDate date) {
        return date.get(ChronoField.ALIGNED_WEEK_OF_MONTH);
    }

    /**
     * @param dateStr Date in format yyyy-MM-dd
     * @return date in format yyyyMM
     */
    public static String getYearMonth(String dateStr) {
        LocalDate tsMonth = LocalDate.parse(dateStr, isoLocalDateFmt);
        return yyyyMMFmt.format(tsMonth);
    }

    /**
     * @param dateStr Date in format yyyy-MM-dd
     * @return date in format MMMM yyyy
     */
    public static String getMonthYear(String dateStr) {
        LocalDate tsMonth = LocalDate.parse(dateStr, isoLocalDateFmt);
        return mmyyFmt.format(tsMonth);
    }

}
