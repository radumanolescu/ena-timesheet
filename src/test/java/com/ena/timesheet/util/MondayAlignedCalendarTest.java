package com.ena.timesheet.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class MondayAlignedCalendarTest {

    @Test
    void testWeekOfMonth_January2024() {
        // January 1, 2024 is a Monday
        LocalDate anchor = LocalDate.of(2024, 1, 15);
        MondayAlignedCalendar cal = new MondayAlignedCalendar(anchor);

        assertEquals(1, cal.getWeekOfMonth(LocalDate.of(2024, 1, 1)).intValue());
        assertEquals(1, cal.getWeekOfMonth(LocalDate.of(2024, 1, 7)).intValue());
        assertEquals(2, cal.getWeekOfMonth(LocalDate.of(2024, 1, 8)).intValue());
        assertEquals(5, cal.getWeekOfMonth(LocalDate.of(2024, 1, 29)).intValue());
    }

    @Test
    void testWeekOfMonth_March2023() {
        // March 1, 2023 is a Wednesday
        LocalDate anchor = LocalDate.of(2023, 3, 10);
        MondayAlignedCalendar cal = new MondayAlignedCalendar(anchor);

        assertEquals(1, cal.getWeekOfMonth(LocalDate.of(2023, 3, 1)).intValue());
        assertEquals(1, cal.getWeekOfMonth(LocalDate.of(2023, 3, 5)).intValue());
        assertEquals(2, cal.getWeekOfMonth(LocalDate.of(2023, 3, 6)).intValue());
        assertEquals(5, cal.getWeekOfMonth(LocalDate.of(2023, 3, 27)).intValue());
        assertNull(cal.getWeekOfMonth(LocalDate.of(2023, 2, 28))); // Not in March
    }

    @Test
    void testWeekOfMonth_April2025() {
        // April 1, 2025 is a Tuesday
        LocalDate anchor = LocalDate.of(2025, 4, 15);
        MondayAlignedCalendar cal = new MondayAlignedCalendar(anchor);

        assertEquals(1, cal.getWeekOfMonth(LocalDate.of(2025, 4, 1)).intValue());
        assertEquals(1, cal.getWeekOfMonth(LocalDate.of(2025, 4, 6)).intValue());
        assertEquals(2, cal.getWeekOfMonth(LocalDate.of(2025, 4, 7)).intValue());
        assertEquals(5, cal.getWeekOfMonth(LocalDate.of(2025, 4, 28)).intValue());
        assertNull(cal.getWeekOfMonth(LocalDate.of(2025, 3, 31))); // Not in April
    }
}

// mvn test -Dtest="com.ena.timesheet.util.MondayAlignedCalendarTest"