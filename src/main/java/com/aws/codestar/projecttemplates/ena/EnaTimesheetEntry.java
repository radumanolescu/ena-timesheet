package com.aws.codestar.projecttemplates.ena;

import com.aws.codestar.projecttemplates.json.JsonMapped;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.aws.codestar.projecttemplates.util.Text.parseFloat;
import static com.aws.codestar.projecttemplates.util.Text.parseXlTime;
import static com.aws.codestar.projecttemplates.util.Time.hoursBetween;

public class EnaTimesheetEntry extends JsonMapped<EnaTimesheetEntry> {

    public EnaTimesheetEntry() {
    }

    public EnaTimesheetEntry(LocalDate month, List<String> row) {

        StringBuilder err = new StringBuilder();
        this.month = month;
        // Project ID and Activity are concatenated with a # in the spreadsheet
        String projectActivity = row.get(0);
        String[] pa = projectActivity.split("#");
        if (pa.length == 2) {
            this.project_id = pa[0];
            this.activity = pa[1];
        } else {
            this.project_id = "";
            this.activity = "";
            err.append("ProjectActivity must be in the form 'ProjectID#Activity', but was '")
                    .append(projectActivity).append("'. ");
        }
        // day of month
        Map.Entry<Float, String> dayOfMonth = parseFloat("DayOfMonth", row.get(1));
        this.day = dayOfMonth.getKey().intValue();
        err.append(dayOfMonth.getValue());
        // start time
        Map.Entry<LocalTime, String> startTime = parseXlTime("StartTime", row.get(2));
        this.start = startTime.getKey();
        err.append(startTime.getValue());
        // end time
        Map.Entry<LocalTime, String> endTime = parseXlTime("EndTime", row.get(3));
        this.end = endTime.getKey();
        err.append(endTime.getValue());
        // hours
        this.hours = hoursBetween(start, end);
        // description
        this.description = row.get(5);
        // errors, if any
        this.error = err.toString();
    }


    public EnaTimesheetEntry(LocalDate month, String project_id, String activity,
                             Integer day, LocalTime start, LocalTime end,
                             Float hours, String description, String error) {

        this.month = month;
        this.project_id = project_id;
        this.activity = activity;
        this.day = day;
        this.start = start;
        this.end = end;
        this.hours = hours;
        this.description = description;
        this.error = error;
    }

    public LocalDate getMonth() {
        return month;
    }

    public void setMonth(LocalDate month) {
        this.month = month;
    }

    public String getProject_id() {
        return project_id;
    }

    public void setProject_id(String project_id) {
        this.project_id = project_id;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public LocalTime getStart() {
        return start;
    }

    public void setStart(LocalTime start) {
        this.start = start;
    }

    public LocalTime getEnd() {
        return end;
    }

    public void setEnd(LocalTime end) {
        this.end = end;
    }

    public Float getHours() {
        return hours;
    }

    public void setHours(Float hours) {
        this.hours = hours;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    private LocalDate month;
    private String project_id;
    private String activity;
    private Integer day;
    private LocalTime start;
    private LocalTime end;
    private Float hours;
    private String description;
    private String error;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EnaTimesheetEntry that = (EnaTimesheetEntry) o;
        return month.equals(that.month) && day.equals(that.day) && start.equals(that.start);
    }

    @Override
    public int hashCode() {
        return Objects.hash(month, day, start);
    }
}
