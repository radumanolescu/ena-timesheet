package com.ena.timesheet.controller;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public abstract class ControllerBase {
    public static final DateTimeFormatter isoLocalDateFmt = DateTimeFormatter.ISO_LOCAL_DATE;
    public static final DateTimeFormatter yyyyMMFmt = DateTimeFormatter.ofPattern("yyyyMM");
    protected static final DateTimeFormatter mmyyFmt = DateTimeFormatter.ofPattern("MMMM yyyy");

    protected byte[] getBytes(InputStream inputStream) {
        byte[] bytes = null;
        try {
            bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
        } catch (Exception e) {
            System.out.println("Error converting InputStream to byte[]");
        }
        return bytes;
    }
    /**
     * @param dateStr Date in format yyyy-MM-dd
     * @return date in format yyyyMM
     * */
    protected String getYearMonth(String dateStr) {
        LocalDate tsMonth = LocalDate.parse(dateStr, isoLocalDateFmt);
        return "" + Integer.parseInt(yyyyMMFmt.format(tsMonth));
    }

    protected String getMonthYear(String dateStr) {
        LocalDate tsMonth = LocalDate.parse(dateStr, isoLocalDateFmt);
        return mmyyFmt.format(tsMonth);
    }

}
