package com.ena.timesheet.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

public class Text {

    /**
     * Replace non-ASCII characters with spaces
     *
     * @param s input string
     * @return input string where all non-ASCII characters have been replaced with spaces
     */
    public static String replaceNonAscii(String s) {
        // Replace all non-ASCII characters with a space
        s = s.replaceAll("[^\\x00-\\x7F]", " ");
        return s;
    }

    /**
     * Replace all quotes in a string
     *
     * @param s input string
     * @return input string where all quotes have been replaced
     */
    public static String unquote(String s) {
        // Replace all double quotes with an empty string
        s = s.replaceAll("\"", "");
        // Replace all single quotes with a backtick
        s = s.replaceAll("'", "`");
        return s;
    }

    public static Map.Entry<Integer, String> parseInt(String name, String s) {
        try {
            return new HashMap.SimpleEntry<>(Integer.parseInt(s), "");
        } catch (NumberFormatException e) {
            return new HashMap.SimpleEntry<>(0, name + " is not a number: " + s + ". ");
        }
    }

    public static Map.Entry<LocalTime, String> parseXlTime(String name, String s) {
        String[] parts = s.split(" "); // "Sun Dec 31 09:30:00 EST 1899"
        if (parts.length == 6) {
            return parseTime(name, parts[3]);
        } else {
            return new HashMap.SimpleEntry<>(LocalTime.parse("00:00"), name + " is not a time: " + s + ". ");
        }
    }

    public static Map.Entry<LocalTime, String> parseTime(String name, String s) {
        try {
            return new HashMap.SimpleEntry<>(LocalTime.parse(s), "");
        } catch (DateTimeParseException e) {
            return new HashMap.SimpleEntry<>(LocalTime.parse("00:00"), name + " is not a time: " + s + ". ");
        }
    }

    public static Map.Entry<Float, String> parseFloat(String name, String s) {
        try {
            return new HashMap.SimpleEntry<>(Float.parseFloat(s), "");
        } catch (NumberFormatException e) {
            return new HashMap.SimpleEntry<>(0.0f, name + " is not a number: " + s + ". ");
        }
    }

    public static String stackFormatter1(Exception e) {
        StringBuilder sb = new StringBuilder();
        sb.append(e.getMessage()).append("\n");
        for (StackTraceElement ste : e.getStackTrace()) {
            sb.append(ste.toString()).append("\n");
        }
        return sb.toString();
    }

    public static String stackFormatter2(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

    public static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

}
