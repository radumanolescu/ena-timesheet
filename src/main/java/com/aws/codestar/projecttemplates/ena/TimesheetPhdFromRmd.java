package com.aws.codestar.projecttemplates.ena;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class TimesheetPhdFromRmd {

    public TimesheetPhdFromRmd(LocalDate tsMonth) {
        this.tsMonth = tsMonth;
    }

    // Month of the timesheet
    private LocalDate tsMonth;

    /**
     * Translated from R by ChatGPT
     * Convert the following fragment from the R programming language to Java:
     */
    public static void part1(String[] args) {
        // Get today's date
        Date today = new Date();

        // Calculate one month ago from today
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.add(Calendar.MONTH, -1);
        Date oneMonthAgo = calendar.getTime();

        // Get the number of days in the previous month
        calendar.setTime(oneMonthAgo);
        int numDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Roll back to the first day of the previous month
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date dayFst = calendar.getTime();

        // Roll forward to the last day of the current month
        calendar.setTime(today);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date dayLst = calendar.getTime();

        // Create a calendar of the month
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
        String[] weekDays = new String[]{"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        int weekNum = 1;
        Date[] datesOfMonth = new Date[numDays];
        String[] datesMdy = new String[numDays];
        String[] weekDaysOfMonth = new String[numDays];
        int[] weekOfMonth = new int[numDays];

        calendar.setTime(dayFst);
        for (int i = 0; i < numDays; i++) {
            datesOfMonth[i] = calendar.getTime();
            datesMdy[i] = sdf.format(datesOfMonth[i]);
            weekDaysOfMonth[i] = weekDays[calendar.get(Calendar.DAY_OF_WEEK) - 1];
            weekOfMonth[i] = weekNum;

            if (weekDaysOfMonth[i].equals("Monday")) {
                weekNum++;
            }

            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        // Create a data frame for the calendar
        String[] columnNames = new String[]{"dates.of.month", "dates.mdy", "week.days", "week.of.month"};
        String[][] data = new String[numDays][4];
        for (int i = 0; i < numDays; i++) {
            data[i][0] = sdf.format(datesOfMonth[i]);
            data[i][1] = datesMdy[i];
            data[i][2] = weekDaysOfMonth[i];
            data[i][3] = String.valueOf(weekOfMonth[i]);
        }

        // Format dates
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM");
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
        SimpleDateFormat monthDayYearFormat = new SimpleDateFormat("MMMM dd, yyyy");
        String timesheetMonth = monthFormat.format(oneMonthAgo);
        String timesheetYear = yearFormat.format(oneMonthAgo);
        String monthDayYear = monthDayYearFormat.format(today);
    }

    public static void part2(String[] args) {
        String timesheetFile = "timesheet_phd.csv";
        String projectKeyFile = "project_key.csv";

        // Read input data
        String[] timesheetCols = {"character", "character", "integer", "NULL", "NULL", "numeric", "character"};
        String[][] timeTable = readTable(timesheetFile, true, "\t", "\"", timesheetCols, false);

        // 1:project_id, 2:project, 3:activity, 4:day, 5:hours, 6:description
        sortTable(timeTable, 3, 0, timeTable[0].length - 1);
        int N = timeTable.length;

        String[][] projKey = readTable(projectKeyFile, false, "\t", null, new String[]{"character", "character"}, false);

        // Enrich daily data
        int[] entryId = new int[N];
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
        Date dayFst = new Date(); // Assuming dayFst is already defined
        double[] ratePerEntry = new double[N];
        String[] project = new String[N];
        double[] chargePerEntry = new double[N];
        int[] week = new int[N];

        for (int i = 0; i < N; i++) {
            entryId[i] = i + 1;
            // ToDo: make it compile
            //te dd = new Date(dayFst.getTime() + (timeTable[i][3] - 1) * 24 * 60 * 60 * 1000L);
            Date dd = new Date(); // ToDo: make it compile
            ratePerEntry[i] = 60;
            project[i] = "";
            chargePerEntry[i] = Double.parseDouble(timeTable[i][5]) * ratePerEntry[i];
            timeTable[i] = Arrays.copyOf(timeTable[i], timeTable[i].length + 6);
            timeTable[i][timeTable[i].length - 6] = Integer.toString(entryId[i]);
            timeTable[i][timeTable[i].length - 5] = sdf.format(dd);
            timeTable[i][timeTable[i].length - 4] = String.format("$%.0f/hr", ratePerEntry[i]);
            timeTable[i][timeTable[i].length - 3] = String.format("$%.2f", chargePerEntry[i]);
            week[i] = -1;
        }

        for (int i = 0; i < N; i++) {
            String entryMdy = timeTable[i][timeTable[i].length - 5];
            for (int j = 0; j < N; j++) {
                if (entryMdy.equals(timeTable[j][timeTable[j].length - 5])) {
                    week[i] = Integer.parseInt(projKey[j][projKey[j].length - 1]);
                    break;
                }
            }
        }

        // Populate main display frame
        // UNFINISHED
    }

    private static String[][] readTable(String timesheetFile, boolean a, String sep, String delim, String[] timesheetCols, boolean b) {
        return null; // ToDo
    }

    private static void sortTable(String[][] timeTable, int i, int i0, int length) {
        // ToDo
    }

    // ---------- ---------- ---------- ---------- ---------- ---------- //

    /**
     * Date-related details
     */
    public int daysInMonth() {
        Calendar calendar = Calendar.getInstance();
        Date date = Date.from(tsMonth.atStartOfDay(ZoneId.systemDefault()).toInstant());
        calendar.setTime(date);
        // Get the number of days in the previous month
        int numDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        return numDays;
    }

/* R code notes
# Inputs:
# timesheet_phd.csv: ENA timesheet; tab-separated, with headers
# project_id	activity	day	start	end	hours	description

In the ENA timesheet, the columns of the CSV file are:
# project_id, activity, day, start, end, hours, description
Read the file and ignore start, end
#                    project_id,  activity,    day,       start,  end,    hours,     description
timesheet.cols <- c("character", "character", "integer", "NULL", "NULL", "numeric", "character")

A table (matrix) of the ENA timesheet, with cols: (project_id, activity, day, hours, description)
time.table <- read.table("timesheet_phd.csv", header = TRUE, sep = "\t", quote="\"", colClasses = timesheet.cols, stringsAsFactors=FALSE)

This is probably wrong, ignore it:
# 1:project_id, 2:project, 3:activity, 4:day, 5:hours, 6:description
This is probably correct:
# 1:project_id, 2:activity, 3:day, 4:hours, 5:description

Sort the table by (day, project_id)
time.table <- time.table[order(time.table$day, time.table$project_id), ]

The number of entries in the ENA timesheet
N <- nrow(time.table)

Vector of entry IDs, starting from 1
entry.id <- 1:N

Vector of dates for each ENA entry, ie the actual date of the entry
dd <- rep(dayFst, N) %m+% days(time.table$day - 1)

Table of the ENA timesheet, with additional cols: (project, dd, rate, mdy, description, entry.id, charge.per.entry, charge)
time.table <- cbind(time.table, project, dd, rate, dates.mdy, entry.id, charge.per.entry, charge,  stringsAsFactors=FALSE)

This is now obsolete: ensure all project IDs from ENA are in the project key from PHD
time.table$project <- proj.key[match(time.table$project_id, proj.key$V1),2]

Reconfigure the main table to contain the columns below
time.table <- time.table[, c("entry.id", "project_id", "project", "activity", "day", "dd", "dates.mdy", "hours", "rate", "description", "charge.per.entry", "charge")]

Add a column for the week number (week of month). Weeks start on Monday.
time.table <- cbind(time.table, week)

In the "display table", rows are added at the end, but with entry.id = lastGroupEntryId + 0.1 / 0.2 / 0.3 / etc, so they sort below each group.

*/

}

