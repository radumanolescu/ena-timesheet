package com.aws.codestar.projecttemplates.util;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * A calendar that is aligned to Mondays, and has a week number for each day
 */
public class MondayAlignedCalendar {
    /**
     * @param anchorDate A date within a month, from which we derive the month for the calendar
     */
    public MondayAlignedCalendar(LocalDate anchorDate) {
        this.anchorDate = anchorDate;
        LocalDate date = anchorDate.withDayOfMonth(1);
        int week = 1;
        while (date.getMonthValue() == anchorDate.getMonthValue()) {
            weekOfMonth.put(date, week);
            date = date.plusDays(1);
            if (date.getDayOfWeek().getValue() == 1) {
                week++;
            }
        }
    }

    private LocalDate anchorDate;

    private Map<LocalDate, Integer> weekOfMonth = new HashMap<>();

    public Integer getWeekOfMonth(LocalDate date) {
        return weekOfMonth.get(date);
    }
}
