package com.ena.timesheet.ena;

import com.ena.timesheet.util.MondayAlignedCalendar;
import com.ena.timesheet.util.Time;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static com.ena.timesheet.xl.XlUtil.getLocalTime;
import static com.ena.timesheet.xl.XlUtil.stringValue;

public class EnaTsEntry implements Comparable<EnaTsEntry> {

    public EnaTsEntry() {
    }

    /**
     * Expected row format: projectId#activity, day, start, end, hours, description
     */
    public EnaTsEntry(int lineId, LocalDate month, Row row) {
        this.entryId = (float) lineId;
        this.month = month;
        this.calendar = new MondayAlignedCalendar(month);

        StringBuilder err = new StringBuilder();
        int cellId = 0;
        int validCells = 0;
        for (Cell cell : row) {
            switch (cellId) {
                case 0: // projectId#activity
                    try {
                        String projectActivity = stringValue(cell);
                        String[] pa = projectActivity.split("#");
                        if (pa.length == 2) {
                            this.projectId = pa[0];
                            this.activity = pa[1];
                            validCells++;
                        } else {
                            this.projectId = "";
                            this.activity = "";
                            err.append("ProjectActivity must be in the form 'ProjectID#Activity', but was '")
                                    .append(projectActivity).append("'. ");
                        }
                    } catch (Exception e) {
                        err.append("ProjectActivity must be in the form 'ProjectID#Activity'. ");
                    }
                    break;
                case 1: // day of month
                    try {
                        this.day = (int) cell.getNumericCellValue();
                        this.date = month.withDayOfMonth(day);
                        validCells++;
                    } catch (Exception e) {
                        err.append("Day must be a number. ");
                    }
                    break;
                case 2: // start time
                    try {
                        this.start = getLocalTime(cell);
                        validCells++;
                    } catch (Exception e) {
                        err.append("Start time must be a time. ");
                    }
                    break;
                case 3: // end time
                    try {
                        this.end = getLocalTime(cell);
                        validCells++;
                    } catch (Exception e) {
                        err.append("End time must be a time. ");
                    }
                    break;
                case 4: // hours
                    try {
                        this.hours = (float) cell.getNumericCellValue();
                        validCells++;
                    } catch (Exception e) {
                        err.append("Hours must be a number. ");
                    }
                    break;
                case 5: // description
                    try {
                        this.description = stringValue(cell);
                        if (this.description.isEmpty()) {
                            err.append("Description must be non-empty. ");
                        } else {
                            validCells++;
                        }
                    } catch (Exception e) {
                        err.append("Description must be non-empty. ");
                    }
                    break;
            }
            cellId++;
        }
        if (this.hours != null) {
            this.charge = hours * hourlyRate;
        }
        this.error = err.toString();
        this.validCells = validCells;
    }

    private static final DecimalFormat decimalFormat = new DecimalFormat("#.##");
    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM/dd/yy");


    // ToDo: ideally, this should be calculated based on project and activity
    protected static final Float hourlyRate = 60.0f;

    private MondayAlignedCalendar calendar;

    private LocalDate month;
    private String projectId = "";
    private String activity = "";
    private Integer day;
    private LocalTime start;
    private LocalTime end;
    protected Float hours;
    private String description = "";
    protected Float charge;
    private String error = "";
    private int validCells;

    /**
     * This is a Float in order to allow new entries to be inserted in the middle of the list
     */
    protected Float entryId;
    private LocalDate date;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EnaTsEntry that = (EnaTsEntry) o;
        return month.equals(that.month) && day.equals(that.day) && start.equals(that.start);
    }

    @Override
    public int hashCode() {
        return Objects.hash(month, day, start);
    }

    @Override
    public int compareTo(EnaTsEntry that) {
        return this.sortKey().compareTo(that.sortKey());
    }

    public String toString() {
        return entryId + ", " + projectId + "," + activity + ", " +
                getDate() + ", " + hours + ", " +
                start + ", " + end + ", " + hourlyRate + ", " +
                getDescription() + ", " + charge;
    }

    public LocalDate getMonth() {
        return month;
    }

    public String getProjectId() {
        return projectId;
    }

    protected void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getActivity() {
        return activity;
    }

    protected void setActivity(String activity) {
        this.activity = activity;
    }

    public Integer getDay() {
        return day;
    }

    public LocalTime getStart() {
        return start;
    }

    public LocalTime getEnd() {
        return end;
    }

    public String getHours() {
        return decimalFormat.format(hours);
    }

    protected void setHours(Float hours) {
        this.hours = hours;
    }

    public String getRate() {
        return "$" + decimalFormat.format(hourlyRate) + "/hr";
    }

    public String getDescription() {
        return description;
    }

    protected void setDescription(String description) {
        this.description = description;
    }

    public String getCharge() {
        return String.format("$%.2f", charge);
    }

    protected void setCharge(Float charge) {
        this.charge = charge;
    }

    public String getError() {
        return error;
    }

    public Float getEntryId() {
        return entryId;
    }

    protected void setEntryId(Float entryId) {
        this.entryId = entryId;
    }

    public String getDate() {
        return dateFormat.format(date);
    }

    public int getValidCells() {
        return validCells;
    }

    public int getWeekOfMonth() {
        return calendar.getWeekOfMonth(date);
    }

    public String sortKey() {
        return String.format("%05d%s", day, projectId);
    }
}
