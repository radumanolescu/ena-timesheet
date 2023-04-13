package com.aws.codestar.projecttemplates.ena;

import java.util.Objects;

public class InvoiceActivityEntry {
    public InvoiceActivityEntry() {
    }

    public InvoiceActivityEntry(String project, String activity, String date, String hours, String rate, String description, String charge) {
        this.project = project;
        this.activity = activity;
        this.date = date;
        this.hours = hours;
        this.rate = rate;
        this.description = description;
        this.charge = charge;
    }

    private String project;
    private String activity;
    private String date;
    private String hours;
    private String rate;
    private String description;
    private String charge;


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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCharge() {
        return charge;
    }

    public void setCharge(String charge) {
        this.charge = charge;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InvoiceActivityEntry that = (InvoiceActivityEntry) o;
        return project.equals(that.project) && activity.equals(that.activity) && date.equals(that.date) && hours.equals(that.hours) && rate.equals(that.rate) && description.equals(that.description) && charge.equals(that.charge);
    }

    @Override
    public int hashCode() {
        return Objects.hash(project, activity, date, hours, rate, description, charge);
    }
}
