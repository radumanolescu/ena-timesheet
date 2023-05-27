package com.ena.timesheet.controller;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public abstract class ControllerBase {
    public static final DateTimeFormatter yyyyMMdd = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static final DateTimeFormatter yyyyMM = DateTimeFormatter.ofPattern("yyyyMM");

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
        LocalDate tsMonth = LocalDate.parse(dateStr, yyyyMMdd);
        return "" + Integer.parseInt(yyyyMM.format(tsMonth));
    }

}
