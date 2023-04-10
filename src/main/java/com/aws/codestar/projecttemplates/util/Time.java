package com.aws.codestar.projecttemplates.util;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class Time {
    public static Float hoursBetween(LocalTime start, LocalTime end) {
        long minutes = ChronoUnit.MINUTES.between(start, end); // Calculate the difference in minutes
        Float hours = minutes / 60.0f; // Convert minutes to hours as a floating-point value
        return hours;
    }
    
}
