package com.aws.codestar.projecttemplates.ena;

import com.aws.codestar.projecttemplates.json.JsonMapped;

import java.time.LocalDate;
import java.util.Objects;

public class InvoiceDayWeekEntry extends JsonMapped<InvoiceDayWeekEntry> {
    public InvoiceDayWeekEntry() {
    }

    private String project;
    private String activity;
    private LocalDate day;
    private Float hours;
    private Float rate;
    private String description;
    private Float charge;

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public LocalDate getDay() {
        return day;
    }

    public void setDay(LocalDate day) {
        this.day = day;
    }

    public Float getHours() {
        return hours;
    }

    public void setHours(Float hours) {
        this.hours = hours;
    }

    public Float getRate() {
        return rate;
    }

    public void setRate(Float rate) {
        this.rate = rate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Float getCharge() {
        return charge;
    }

    public void setCharge(Float charge) {
        this.charge = charge;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InvoiceDayWeekEntry that = (InvoiceDayWeekEntry) o;
        return project.equals(that.project) && activity.equals(that.activity) && day.equals(that.day) && hours.equals(that.hours) && rate.equals(that.rate) && description.equals(that.description) && charge.equals(that.charge);
    }

    @Override
    public int hashCode() {
        return Objects.hash(project, activity, day, hours, rate, description, charge);
    }
}
